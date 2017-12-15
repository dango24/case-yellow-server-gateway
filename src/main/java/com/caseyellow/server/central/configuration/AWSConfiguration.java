package com.caseyellow.server.central.configuration;

import java.io.IOException;

public interface AWSConfiguration {
    String accessKeyID();
    String secretAccessKey();
    String bucketName();
    String healthPath();
    AWSConfiguration buildCredentials() throws IOException;
}