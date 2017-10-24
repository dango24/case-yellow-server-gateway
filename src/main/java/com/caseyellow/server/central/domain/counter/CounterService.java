package com.caseyellow.server.central.domain.counter;

public interface CounterService {
    void addComparisionInfoDetails(String speedTestIdentifier, String fileDownloadIdentifier);
    void decreaseComparisionInfoDetails(String speedTestIdentifier, String fileDownloadIdentifier);
}
