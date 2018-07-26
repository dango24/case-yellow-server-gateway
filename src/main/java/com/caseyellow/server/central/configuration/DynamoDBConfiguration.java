package com.caseyellow.server.central.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class DynamoDBConfiguration {

    @Value("${dynamo.endpoint}")
    private String dynamoServiceEndpoint;

    @Value("${dynamo.region}")
    private String dynamoServiceRegion;

    @Bean
    public AmazonDynamoDB amazonDynamoDB(AWSConfiguration configurationManager) {
        AWSCredentials credentials =
                new BasicAWSCredentials(configurationManager.accessKeyID(),
                                        configurationManager.secretAccessKey());

        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(dynamoServiceEndpoint, dynamoServiceRegion);

        AmazonDynamoDB amazonDynamoDB =
                AmazonDynamoDBClient.builder()
                                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                                    .withEndpointConfiguration(endpointConfiguration)
                                    .build();

        return amazonDynamoDB;
    }
}
