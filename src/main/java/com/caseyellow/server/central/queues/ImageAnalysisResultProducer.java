package com.caseyellow.server.central.queues;

import com.amazon.sqs.javamessaging.SQSMessagingClientConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Slf4j
@Service
@Profile("prod")
public class ImageAnalysisResultProducer implements MessageProducerService {

    @Value("${sqs.image_analyze.queue}")
    private String queueName;

    private JmsTemplate jmsTemplate;
    private ObjectMapper converter;

    @Autowired
    public ImageAnalysisResultProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.converter = new ObjectMapper();
    }

    @Override
    @Retryable(value =  JmsException.class , maxAttempts = 10, backoff = @Backoff(delay = 5000))
    public <T extends Object> void send(MessageType type, T payload)  {
        try {
            QueueMessage message = new QueueMessage(type, converter.writeValueAsString(payload));
            jmsTemplate.send(queueName, (session) -> createMessage(session, message));

        } catch (JsonProcessingException e) {
            log.error(String.format("Failed to convert %s to json, cause: %s", payload, e.getMessage()), e);
        }
    }

    private Message createMessage(Session session, QueueMessage message) throws JMSException {
        try {
            Message createMessage = session.createTextMessage(converter.writeValueAsString(message));
            createMessage.setStringProperty(SQSMessagingClientConstants.JMSX_GROUP_ID, "messageGroup1");
            createMessage.setStringProperty(SQSMessagingClientConstants.JMS_SQS_DEDUPLICATION_ID, "1" + System.currentTimeMillis());
            createMessage.setStringProperty("documentType", message.getClass().getName());

            return createMessage;

        } catch (Exception | Error e) {
            log.error(String.format("Fail to send message %s", e.getMessage()));

            throw new JMSException(e.getMessage());
        }
    }
}
