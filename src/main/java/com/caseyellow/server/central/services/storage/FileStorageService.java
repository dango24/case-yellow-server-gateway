package com.caseyellow.server.central.services.storage;

import com.caseyellow.server.central.domain.test.model.PreSignedUrl;

import java.io.File;

public interface FileStorageService {
    File getFile(String identifier);
    String uploadFile(String path, File fileToUpload);
    String uploadFileToBucket(String region, String bucketName, String path, File fileToUpload);
    PreSignedUrl generatePreSignedUrl(String fileKey);
    boolean isObjectExist(String path);
}
