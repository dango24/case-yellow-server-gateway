package com.caseyellow.server.central.services.storage;

import com.caseyellow.server.central.domain.test.model.PreSignedUrl;

import java.io.File;

public interface FileStorageService {
    String uploadFile(String userIP, File file);
    File getFile(String identifier);
    PreSignedUrl generatePreSignedUrl(String userIP, String fileName);
}
