package com.caseyellow.server.central.configuration;

import java.io.IOException;

public interface AWSConfiguration {
    String getAccessKeyID();
    String getSecretAccessKey();
    String getBucketName();
    String healthyPath();
    AWSConfigurationImpl buildCredentials() throws IOException;
}