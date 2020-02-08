package com.caseyellow.server.central.services.storage;

import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Profile("dev")
public class FileStorageStub implements FileStorageService {

    @Override
    public File getFile(String identifier) {
        // Do nothing
        return null;
    }

    @Override
    public String uploadFile(String path, File fileToUpload) {
        return null;
    }

    @Override
    public String uploadFileToBucket(String region, String bucketName, String path, File fileToUpload) {
        return null;
    }


    @Override
    public PreSignedUrl generatePreSignedUrl(String fileKey) {
        return new PreSignedUrl(null, null);
    }

    @Override
    public boolean isObjectExist(String path) {
        return false;
    }

    @Override
    public boolean isObjectExist(String bucket, String path) {
        return false;
    }
}
