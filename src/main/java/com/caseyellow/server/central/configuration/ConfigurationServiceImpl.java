package com.caseyellow.server.central.configuration;

import com.caseyellow.server.central.exceptions.ConfigurationException;
import com.caseyellow.server.central.exceptions.IORuntimeException;
import org.apache.log4j.Logger;
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
public class ConfigurationServiceImpl implements AWSConfiguration, GoogleVisionConfiguration {

    private Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);

    @Value("${S3_bucket_name}")
    private String bucketName;

    @Value("${credentials_path}")
    private String credentialsPath;

    @Value("${healthy_path}")
    private String healthyPath;

    private String accessKeyID;
    private String secretAccessKey;
    private String googleVisionKey;

    @Override
    public String accessKeyID() {
        return accessKeyID;
    }

    @Override
    public String secretAccessKey() {
        return secretAccessKey;
    }

    @Override
    public String googleVisionKey() {
        return googleVisionKey;
    }

    @Override
    public String bucketName() {
        return bucketName;
    }

    @Override
    public String healthPath() {
        return healthyPath;
    }

    @Override
    public AWSConfiguration buildCredentials() throws IOException {
        while(!buildCredentialsFromEncryptedCredentials());

        return this;
    }

    private boolean buildCredentialsFromEncryptedCredentials() throws IOException {
        try {
            String userInput = receiveUserInput();
            String key = encryptSHA512(userInput);
            String decryptedCredentials = decryptCredentials(key);

            parseDecryptedCredentials(decryptedCredentials);

            return true;

        } catch (ConfigurationException e) {
            logger.error("Failed to build credentials, " + e.getMessage(), e);
            return false;
        }
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
        String[] commands = {credentialsPath, key};
        Process process = Runtime.getRuntime().exec(commands);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

        return stdInput.readLine();
    }

    private void parseDecryptedCredentials(String decryptedCredentials) {
        try {
            decryptedCredentials = decryptedCredentials.replaceAll("\\$", "");
            String[] credentials = decryptedCredentials.split(";");
            accessKeyID = credentials[0];
            secretAccessKey = credentials[1];
            googleVisionKey = credentials[2];

        } catch (Exception e) {
            throw new ConfigurationException("Failed to decrypted credentials: " + decryptedCredentials + ", cause: " + e.getMessage(), e);
        }
    }
}
