package com.caseyellow.server.central.configuration;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.caseyellow.server.central.queues.QueueMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Slf4j
@EnableJms
@Profile("prod")
@Configuration
public class JMSConfiguration {

    @Value("${sqs.queue.endpoint}")
    private String endpoint;

    @Bean
    public ConnectionFactory sqsConnectionFactory(AWSConfiguration configurationManager) {
        BasicAWSCredentials awsCreds =
                new BasicAWSCredentials(configurationManager.accessKeyID(), configurationManager.secretAccessKey());

        AmazonSQS amazonSQS =
                AmazonSQSClient.builder()
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, Regions.EU_CENTRAL_1.getName()))
                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                        .build();

        SQSConnectionFactory connectionFactory =
                new SQSConnectionFactory(new ProviderConfiguration(), amazonSQS);

        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("1-1");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        factory.setMessageConverter(messageConverter);

        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter);
        return jmsTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.dateFormat(new ISO8601DateFormat());

        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter() {

            @Override
            protected JavaType getJavaTypeForMessage(Message message) throws JMSException {
                return TypeFactory.defaultInstance().constructType(QueueMessage.class);
            }
        };

        mappingJackson2MessageConverter.setObjectMapper(builder.build());
        mappingJackson2MessageConverter.setTargetType(MessageType.TEXT);

        return mappingJackson2MessageConverter;
    }
}
