package com.caseyellow.server.central.services.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.caseyellow.server.central.configuration.AWSConfiguration;
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

@Service
@Profile("prod")
public class S3FileStorageService implements FileStorageService {

    private Logger logger = Logger.getLogger(S3FileStorageService.class);

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
            AWSCredentials credentials = new BasicAWSCredentials(awsConfiguration.getAccessKeyID(), awsConfiguration.getSecretAccessKey());
            s3Client = AmazonS3ClientBuilder.standard()
                                            .withRegion(Regions.EU_CENTRAL_1)
                                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                                            .build();

        } while (!isHealthy());
    }

    @Override // result.getContentMd5()
    public String uploadFile(String userIP, File fileToUpload) {
        String path = createFileUniquePath(userIP, fileToUpload.getName());
        logger.info("Upload file to s3: " + path);
        s3Client.putObject(new PutObjectRequest(awsConfiguration.getBucketName(), path, fileToUpload));

        return path;
    }

    @Override
    public File getFile(String identifier) {
        String fileName = identifier.split("/")[1];
        return getFileFromS3(identifier, fileName);
    }

    private File getFileFromS3(String identifier, String fileName) {
        logger.info("Fetch file from s3: " + identifier);
        File newFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        S3Object object = s3Client.getObject(new GetObjectRequest(awsConfiguration.getBucketName(), identifier));

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
        String uniquePath = new StringBuilder(String.valueOf(System.currentTimeMillis())).reverse().toString();
        String userIdentifier = userIP.replaceAll("\\.", "");

        return userIdentifier + "/" + uniquePath + fileName.substring(separationIndex);
    }

    public boolean isHealthy() {
        try {
            getFileFromS3(awsConfiguration.healthyPath(), awsConfiguration.healthyPath());
            return true;

        } catch (Exception e) {
            logger.error("Connection not healthy, Read from s3 failed, " + e.getMessage(), e);
            return false;
        }
    }
}
