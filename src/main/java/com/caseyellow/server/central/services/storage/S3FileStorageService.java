package com.caseyellow.server.central.services.storage;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.caseyellow.server.central.configuration.AWSConfiguration;
import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.exceptions.IORuntimeException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
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

@Service
@Profile("prod")
public class S3FileStorageService implements FileStorageService {

    private Logger logger = Logger.getLogger(S3FileStorageService.class);

    private static final String SUFFIX = ".png";

    private AmazonS3 s3Client;
    private AWSConfiguration awsConfiguration;

    @Autowired
    public S3FileStorageService(AWSConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;
    }

    @PostConstruct
    public void init() throws IOException {
        do {
            awsConfiguration.buildCredentials();
            AWSCredentials credentials = new BasicAWSCredentials(awsConfiguration.accessKeyID(),
                                                                 awsConfiguration.secretAccessKey());
            s3Client = AmazonS3ClientBuilder.standard()
                                            .withRegion(Regions.EU_CENTRAL_1)
                                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                                            .build();

        } while (!isHealthy());
    }

    @Override
    public File getFile(String identifier) {
        String fileName = identifier.split("/")[1];
        return getFileFromS3(identifier, fileName);
    }

    @Override
    public PreSignedUrl generatePreSignedUrl(String userIP, String fileName) {
        String uniquePath = createFileUniquePath(userIP, fileName);
        return generatePreSignedUrl(uniquePath);
    }

    private File getFileFromS3(String identifier, String fileName) {
        logger.info("Fetch file from s3: " + identifier);
        File newFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        S3Object object = s3Client.getObject(new GetObjectRequest(awsConfiguration.bucketName(), identifier));

        try (InputStream objectData = object.getObjectContent()) {
            FileUtils.copyInputStreamToFile(objectData, newFile);

        } catch (IOException e) {
            logger.error("Failed to get file from S3, " + e.getMessage(), e);
            throw new IORuntimeException("Failed to get file from S3, " + e.getMessage(), e);
        }

        return newFile;
    }

    private String createFileUniquePath(String userIP, String fileName) {
        int separationIndex = fileName.indexOf("_");
        separationIndex = separationIndex >= 0 ? separationIndex : 0;
        String uniquePath = new StringBuilder(String.valueOf(System.currentTimeMillis())).reverse().toString();
        String userIdentifier = userIP.replaceAll("\\.", "");

        return userIdentifier + "/" + uniquePath + ";" + fileName.substring(separationIndex) + SUFFIX;
    }

    public boolean isHealthy() {
        if (s3Client.doesObjectExist(awsConfiguration.bucketName(), awsConfiguration.healthPath())) {
            logger.info("The connection to s3 is healthy");
            return true;
        } else {
            logger.error("Healthy check to s3 failed");
            return false;
        }
    }

    public PreSignedUrl generatePreSignedUrl(String objectKey) {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + TimeUnit.HOURS.toMillis(1)); // Add 1 hour.

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(awsConfiguration.bucketName(), objectKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
        generatePresignedUrlRequest.setExpiration(expiration);
        URL preSignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);


        return new PreSignedUrl(preSignedUrl, objectKey);
    }
}
