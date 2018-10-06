package com.caseyellow.server.central.configuration;

import com.caseyellow.server.central.exceptions.ConfigurationException;
import com.caseyellow.server.central.exceptions.IORuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
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

    @Value("${health_path}")
    private String healthPath;

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
        return healthPath;
    }

    @PostConstruct
    private void buildCredentialsFromEncryptedCredentials() throws IOException {
        String key = encryptSHA512(encryptionKey);
        String decryptedCredentials = decryptCredentials(key);
        parseDecryptedCredentials(decryptedCredentials);
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
