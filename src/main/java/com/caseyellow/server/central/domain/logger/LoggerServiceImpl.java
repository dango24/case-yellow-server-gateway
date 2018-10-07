package com.caseyellow.server.central.domain.logger;

import org.springframework.stereotype.Service;

@Service
public class LoggerServiceImpl implements LoggerService {

    @Override
    public void uploadLogData(String user, LogData logData) {
        System.out.println("Dangooo");
    }
}
