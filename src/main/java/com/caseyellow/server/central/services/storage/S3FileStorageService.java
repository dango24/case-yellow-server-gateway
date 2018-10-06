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
import java.util.concurrent.TimeUnit;

import static com.caseyellow.server.central.common.Utils.createTempFilename;

@Slf4j
@Service
@Profile("prod")
public class S3FileStorageService implements FileStorageService {

    private AmazonS3 s3Client;
    private AWSConfiguration awsConfiguration;

    @Autowired
    public S3FileStorageService(AWSConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;
    }

    @PostConstruct
    public void init() throws IOException {
        do {
            AWSCredentials credentials = new BasicAWSCredentials(awsConfiguration.accessKeyID(),
                                                                 awsConfiguration.secretAccessKey());
            s3Client = AmazonS3ClientBuilder.standard()
                                            .withRegion(Regions.EU_CENTRAL_1)
                                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                                            .build();

        } while (!isHealthy());
    }

    @Override
    public String uploadFile(String path, File fileToUpload) {
        s3Client.putObject(new PutObjectRequest(awsConfiguration.bucketName(), path, fileToUpload));
        return getResourceUrl(path);
    }

    @Override
    public File getFile(String identifier) {
        log.info("Fetch file from s3: " + identifier);
        String tempFilename = createTempFilename(identifier);
        File newFile = new File(System.getProperty("java.io.tmpdir"), tempFilename);
        S3Object object = s3Client.getObject(new GetObjectRequest(awsConfiguration.bucketName(), identifier));

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
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + TimeUnit.HOURS.toMillis(1)); // Add 1 hour.

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(awsConfiguration.bucketName(), objectKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
        generatePresignedUrlRequest.setExpiration(expiration);
        URL preSignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);


        return new PreSignedUrl(preSignedUrl, objectKey);
    }

    @Override
    public boolean isObjectExist(String path) {
        return s3Client.doesObjectExist(awsConfiguration.bucketName(), path);
    }

    public boolean isHealthy() {
        if (s3Client.doesObjectExist(awsConfiguration.bucketName(), awsConfiguration.healthPath())) {
            log.info("The connection to s3 is healthy");
            return true;
        } else {
            log.error("S3 Health check failed");
            return false;
        }
    }

    private String getResourceUrl(String path) {
        return ((AmazonS3Client)s3Client).getResourceUrl(awsConfiguration.bucketName(), path);
    }
}
