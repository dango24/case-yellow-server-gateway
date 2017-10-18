package com.caseyellow.server.central.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Profile("dev")
public class FileUploadStub implements FileUploadService {

    @Override
    public String uploadFile(String userIP, File file) {
        return "Do Nothing";
    }
}
