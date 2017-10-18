package com.caseyellow.server.central.services.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.caseyellow.server.central.exceptions.IORuntimeException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
@Profile("prod")
@PropertySource("aws_access.yml")
public class S3FileStorageService implements FileStorageService {

    @Value("${aws_access_key_id}")
    private String accessKeyID;

    @Value("${aws_secret_access_key}")
    private String secretAccessKey;

    @Value("${S3_bucket_name}")
    private String bucketName;

    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyID, secretAccessKey);
        s3Client = new AmazonS3Client(credentials);
    }

    @Override // result.getContentMd5()
    public String uploadFile(String userIP, File fileToUpload) {
        String path = createFileUniquePath(userIP, fileToUpload.getName());
        s3Client.putObject(new PutObjectRequest(bucketName, path, fileToUpload));

        return path;
    }

    @Override
    public File getFile(String identifier) {
        File newFile = new File(System.getProperty("java.io.tmpdir"), identifier.split(File.separator)[1]);
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, identifier));

        try (InputStream objectData = object.getObjectContent()) {
            FileUtils.copyInputStreamToFile(objectData, newFile);

        } catch (IOException e) {
            throw new IORuntimeException("Failed to get file from S3, " + e.getMessage(), e);
        }

        return newFile;
    }

    private String createFileUniquePath(String userIP, String fileName) {
        int separationIndex = fileName.indexOf("_");
        String uniquePath = new StringBuilder(String.valueOf(System.currentTimeMillis())).reverse().toString();
        String userIdentifier = userIP.replaceAll("\\.", "");

        return userIdentifier + File.separator + uniquePath + fileName.substring(separationIndex);
    }
}
