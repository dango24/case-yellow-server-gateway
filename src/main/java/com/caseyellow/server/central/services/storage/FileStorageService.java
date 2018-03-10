package com.caseyellow.server.central.services.storage;

import com.caseyellow.server.central.domain.test.model.PreSignedUrl;

import java.io.File;

public interface FileStorageService {
    File getFile(String identifier);
    PreSignedUrl generatePreSignedUrl(String fileKey);
    boolean isObjectExist(String path);
}
