package com.caseyellow.server.central.services.storage;

import com.caseyellow.server.central.exceptions.RequestFailureException;

import java.io.File;

public interface FTPService {
    String uploadFileToCache(String fileName, File fileToUpload) throws RequestFailureException;
}
