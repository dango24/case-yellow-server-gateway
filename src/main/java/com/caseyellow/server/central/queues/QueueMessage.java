package com.caseyellow.server.central.queues;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class QueueMessage {

    @JsonProperty("message_type")
    private MessageType messageType;

    private String payload;
}
