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

    private AmazonS3 s3client;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyID, secretAccessKey);
        s3client = new AmazonS3Client(credentials);
        uploadFile(null);
    }

    @Override
    public String uploadFile(File file) {

        File f = new File("/home/dango/Downloads/dango_p.jpg");

        s3client.putObject(new PutObjectRequest("case-yellow-snapshot", "dango.png", f));

        return null;
    }
}
