package com.caseyellow.server.central.queues;

public interface MessageProducerService {
    <T extends Object> void send(MessageType type, T payload);
}
