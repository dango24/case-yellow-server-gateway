package com.caseyellow.server.central.services;

import com.caseyellow.server.central.services.storage.FileStorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Profile("dev")
public class FileUploadStub implements FileStorageService {

    @Override
    public String uploadFile(String userIP, File file) {
        return "Do Nothing";
    }

    @Override
    public File getFile(String identifier) {
        return null;
    }
}
