package com.caseyellow.server.central.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
@Profile("prod")
@PropertySource("aws_access.yml")
public class S3FileUploadService implements FileUploadService {

    @Value("${aws_access_key_id}")
    private String accessKeyID;

    @Value("${aws_secret_access_key}")
    private String secretAccessKey;

    @Value("${S3_bucket_name}")
    private String S3BucketName;

    private AmazonS3 s3client;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyID, secretAccessKey);
        s3client = new AmazonS3Client(credentials);
    }

    @Override // result.getContentMd5()
    public String uploadFile(String userIP, File fileToUpload) {

        String path = createFileUniquePath(userIP, fileToUpload.getName());
        s3client.putObject(new PutObjectRequest(S3BucketName, path, fileToUpload));

        return path;
    }

    private String createFileUniquePath(String userIP, String fileName) {
        String uniquePath = new StringBuilder(String.valueOf(System.currentTimeMillis())).reverse().toString();
        String userIdentifier = userIP.replaceAll("\\.", "");

        return userIdentifier + File.separator + uniquePath + "_" + fileName;
    }
}
