package com.caseyellow.server.central.common;

import com.caseyellow.server.central.exceptions.IORuntimeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Map;


public interface Utils {

    static Map.Entry<String, File> writeToFile(Map.Entry<String, MultipartFile> snapshot) {
        String identifier = snapshot.getKey();
        MultipartFile file = snapshot.getValue();

        try {
            byte[] bytes = file.getBytes();
            File tempFileLocation = new File(System.getProperty("java.io.tmpdir"), identifier + "_" +file.getOriginalFilename());
            Files.write(tempFileLocation.toPath(), bytes);

            return new AbstractMap.SimpleEntry<>(identifier, tempFileLocation);

        } catch (IOException e) {
            throw new IORuntimeException("Failed to write file from request to tmp dir, " + e.getMessage(), e);
        }
    }

     static double calculateDownloadRateFromMbpsToKBps(double downloadRateInMbps) {
        double MBPerSec = downloadRateInMbps / 8;

        return MBPerSec * Math.pow(2, 10); // Transform to KB
    }
}
