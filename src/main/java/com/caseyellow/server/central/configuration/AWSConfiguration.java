package com.caseyellow.server.central.configuration;

public interface AWSConfiguration {
    String accessKeyID();
    String secretAccessKey();
    String bucketName();
    String healthPath();
}