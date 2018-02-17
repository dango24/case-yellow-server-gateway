package com.caseyellow.server.central.domain.counter;

public interface CounterService {
    void increaseComparisionInfoDetails(String speedTestIdentifier, String fileDownloadIdentifier);
    void decreaseComparisionInfoDetails(String speedTestIdentifier, String fileDownloadIdentifier);
}
