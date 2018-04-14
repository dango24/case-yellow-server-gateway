package com.caseyellow.server.central.queues;

import com.caseyellow.server.central.domain.analyzer.model.AnalyzedImage;
import com.caseyellow.server.central.domain.analyzer.services.ImageAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Profile("prod")
public class SQSConsumer {

    private ObjectMapper objectMapper;
    private ImageAnalyzer imageAnalyzer;

    @Autowired
    public SQSConsumer(ImageAnalyzer imageAnalyzer) {
        this.imageAnalyzer = imageAnalyzer;
        this.objectMapper = new ObjectMapper();
    }

    @JmsListener(destination = "${sqs.central.queue}")
    public void processMessage(@Payload QueueMessage message) throws IOException {
        log.info(String.format("Received message for Processing: %s", message));

        switch (message.getMessageType()) {

            case SNAPSHOT_ANALYZED:
                AnalyzedImage analyzedImage = objectMapper.readValue(message.getPayload(), AnalyzedImage.class);
                imageAnalyzer.updateAnalyzedImageResult(analyzedImage.path, analyzedImage.result);
        }

    }

}
