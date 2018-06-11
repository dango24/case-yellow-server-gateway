package com.caseyellow.server.central.configuration;

import com.caseyellow.server.central.exceptions.ConfigurationException;
import com.caseyellow.server.central.exceptions.IORuntimeException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

    @Value("${encryption_key}")
    private String encryptionKey;

    private String accessKeyID;
    private String secretAccessKey;

    @Override
    public String accessKeyID() {
        return accessKeyID;
    }

    @Override
    public String secretAccessKey() {
        return secretAccessKey;
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
            String key = encryptSHA512(encryptionKey);
            String decryptedCredentials = decryptCredentials(key);

            parseDecryptedCredentials(decryptedCredentials);

            return true;

        } catch (ConfigurationException e) {
            log.error("Failed to build credentials, " + e.getMessage(), e);
            return false;
        }
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

        } catch (Exception e) {
            throw new ConfigurationException("Failed to decrypted credentials: " + decryptedCredentials + ", cause: " + e.getMessage(), e);
        }
    }
}
