package com.caseyellow.server.central.services.storage;

import com.caseyellow.server.central.exceptions.IORuntimeException;

public interface FTPService {
    String uploadFileToCache(String fileName, String filePath) throws IORuntimeException;
}
