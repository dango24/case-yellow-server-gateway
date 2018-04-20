package com.caseyellow.server.central.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.util.Objects.nonNull;

public interface Utils {

    Logger log = Logger.getLogger(Utils.class);

    static byte[] createImageBase64Encode(String imgPath) throws IOException {
        File imageFile = new File(imgPath);
        byte[] imageBase64Encode = Base64.getEncoder().encode(FileUtils.readFileToByteArray(imageFile));

        return imageBase64Encode;
    }

     static double calculateDownloadRateFromMbpsToKBps(double downloadRateInMbps) {
        double MBPerSec = downloadRateInMbps / 8.0;

        return MBPerSec * Math.pow(2, 10); // Transform to KB
    }

    static void deleteFile(File file) {
        try {
            if (nonNull(file) && file.exists()) {
                Files.deleteIfExists(file.toPath());
            }
        } catch (IOException e) {
            log.error(String.format("Failed to delete file: %s", e.getMessage()));
        }
    }

    static void putMDC(String data) {
        MDC.put("correlation-id", data);
    }

    static void removeMDC() {
        MDC.remove("correlation-id");
    }

}
