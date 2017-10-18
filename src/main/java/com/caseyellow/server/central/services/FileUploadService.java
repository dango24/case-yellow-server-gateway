package com.caseyellow.server.central.services;

import java.io.File;

public interface FileUploadService {
    String uploadFile(String userIP, File file);
}
