package com.caseyellow.server.central.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public interface Utils {

    static byte[] createImageBase64Encode(String imgPath) throws IOException {
        File imageFile = new File(imgPath);
        byte[] imageBase64Encode = Base64.getEncoder().encode(FileUtils.readFileToByteArray(imageFile));

        return imageBase64Encode;
    }

     static double calculateDownloadRateFromMbpsToKBps(double downloadRateInMbps) {
        double MBPerSec = downloadRateInMbps / 8;

        return MBPerSec * Math.pow(2, 10); // Transform to KB
    }
}
