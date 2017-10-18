package com.caseyellow.server.central.services.storage;

import java.io.File;

public interface FileStorageService {
    String uploadFile(String userIP, File file);
    File getFile(String identifier);
}
