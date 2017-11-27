package com.caseyellow.server.central.configuration;

import com.caseyellow.server.central.exceptions.IORuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

@Configuration
@Profile("prod")
@PropertySource("aws_access.yml")
public class AWSConfigurationImpl implements AWSConfiguration {

    @Value("${S3_bucket_name}")
    private String bucketName;

    @Value("${credentials_path}")
    private String credentialsPath;

    @Value("${healthy_path}")
    private String healthyPath;

    private String accessKeyID;
    private String secretAccessKey;

    @Override
    public String getAccessKeyID() {
        return accessKeyID;
    }

    @Override
    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }

    @Override
    public String healthyPath() {
        return healthyPath;
    }

    @Override
    public AWSConfigurationImpl buildCredentials() throws IOException {
        buildCredentialsFromEncryptedCredentials();
        return this;
    }

    private void buildCredentialsFromEncryptedCredentials() throws IOException {
        String userInput = receiveUserInput();
        String key = encryptSHA512(userInput);
        String decryptedCredentials = decryptCredentials(key);

        parseDecryptedCredentials(decryptedCredentials);
    }

    private String receiveUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter key:");

        return scanner.nextLine();
    }

    private String encryptSHA512(String target) {
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-512");
            sh.update(target.getBytes());

            return String.format("%0128x", new BigInteger(1, sh.digest()));

        } catch (NoSuchAlgorithmException e) {
            throw new IORuntimeException(e.getMessage(), e);
        }
    }

    private String decryptCredentials(String key) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {credentialsPath, key};
        Process proc = rt.exec(commands);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        return stdInput.readLine();
    }

    private void parseDecryptedCredentials(String decryptedCredentials) {
        decryptedCredentials = decryptedCredentials.replaceAll("\\$", "");
        String[] credentials = decryptedCredentials.split(";");
        accessKeyID = credentials[0];
        secretAccessKey = credentials[1];
    }
}
