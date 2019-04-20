package com.caseyellow.server.central.services.storage;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.caseyellow.server.central.configuration.AWSConfiguration;
import com.caseyellow.server.central.configuration.AWSRegions;
import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.exceptions.IORuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.caseyellow.server.central.common.Utils.createTempFilename;

@Slf4j
@Service
@Profile("prod")
public class S3FileStorageService implements FileStorageService {

    private static final String DEFAULT_REGION = "frankfurt";

    private Map<String, AmazonS3> s3Clients;
    private AWSConfiguration awsConfiguration;
    private AWSRegions awsRegions;


    @Autowired
    public S3FileStorageService(AWSConfiguration awsConfiguration, AWSRegions awsRegions) {
        this.awsConfiguration = awsConfiguration;
        this.awsRegions = awsRegions;
    }

    @PostConstruct
    public void init() throws IOException {
        do {
            AWSCredentials credentials = new BasicAWSCredentials(awsConfiguration.accessKeyID(),
                                                                 awsConfiguration.secretAccessKey());
            s3Clients =
                awsRegions.getRegions()
                          .entrySet()
                          .stream()
                          .collect(Collectors.toMap(Map.Entry::getKey, entry -> createS3Client(entry.getValue(), credentials)));

        } while (!isHealthy());
    }

    private AmazonS3 createS3Client(String region, AWSCredentials credentials) {
        return AmazonS3ClientBuilder.standard()
                                    .withRegion(Regions.fromName(region))
                                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                                    .build();
    }

    @Override
    public String uploadFile(String path, File fileToUpload) {
        return uploadFileToBucket(DEFAULT_REGION, awsConfiguration.bucketName(), path, fileToUpload);
    }

    @Override
    public String uploadFileToBucket(String region, String bucketName, String path, File fileToUpload) {
        AmazonS3 client = s3Clients.get(region);
        client.putObject(new PutObjectRequest(bucketName, path, fileToUpload));
        return getResourceUrl(client, bucketName, path);
    }

    @Override
    public File getFile(String identifier) {
        log.info("Fetch file from s3: " + identifier);
        String tempFilename = createTempFilename(identifier);
        File newFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        AmazonS3 client = s3Clients.get(DEFAULT_REGION);
        S3Object object = client.getObject(new GetObjectRequest(awsConfiguration.bucketName(), identifier));

        try (InputStream objectData = object.getObjectContent()) {
            FileUtils.copyInputStreamToFile(objectData, newFile);

        } catch (IOException e) {
            log.error("Failed to get file from S3, " + e.getMessage(), e);
            throw new IORuntimeException("Failed to get file from S3, " + e.getMessage(), e);
        }

        return newFile;
    }

    @Override
    public PreSignedUrl generatePreSignedUrl(String objectKey) {
        AmazonS3 client = s3Clients.get(DEFAULT_REGION);
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + TimeUnit.HOURS.toMillis(1)); // Add 1 hour.

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(awsConfiguration.bucketName(), objectKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
        generatePresignedUrlRequest.setExpiration(expiration);
        URL preSignedUrl = client.generatePresignedUrl(generatePresignedUrlRequest);


        return new PreSignedUrl(preSignedUrl, objectKey);
    }

    @Override
    public boolean isObjectExist(String path) {
        AmazonS3 client = s3Clients.get(DEFAULT_REGION);
        return client.doesObjectExist(awsConfiguration.bucketName(), path);
    }

    public boolean isHealthy() {
        AmazonS3 client = s3Clients.get(DEFAULT_REGION);
        if (client.doesObjectExist(awsConfiguration.bucketName(), awsConfiguration.healthPath())) {
            log.info("The connection to s3 is healthy");
            return true;
        } else {
            log.error("S3 Health check failed");
            return false;
        }
    }

    private String getResourceUrl(AmazonS3 client, String bucketName, String path) {
        return ((AmazonS3Client) client).getResourceUrl(bucketName, path);
    }
}
