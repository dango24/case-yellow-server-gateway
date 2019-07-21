package com.caseyellow.server.central.services.storage;

import com.caseyellow.server.central.common.Utils;
import com.caseyellow.server.central.exceptions.IORuntimeException;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Profile("prod")
@Service
public class FTPServiceImpl implements FTPService {

    private Logger logger = Logger.getLogger(FTPServiceImpl.class);

    private final static String FTP_COMMAND = "/home/ec2-user/ftp %s %s"; // ftp <File path> <Directory>
    private final static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd_MM_yyyy");

    private final static String DOMAIN = "http://yellow.tempurl.co.il/";


    @Override
    public String uploadFileToCache(String fileName, String filePath) throws IORuntimeException {
        String directory = getDirectory();

        logger.info("Upload file: " + filePath);
        Utils.executeCommand(String.format(FTP_COMMAND, filePath, directory));

        return DOMAIN + directory + "/" + fileName;
    }

    private String getDirectory() {
        return DAY_FORMAT.format(new Date());
    }

    private String renameFile(File origin, String fileName) {
        File newFile = new File(System.getProperty(System.getProperty("java.io.tmpdir"), fileName));
        boolean success = origin.renameTo(newFile);

        if (!success) {
            throw new IORuntimeException(String.format("Failed to rename file, origin: %s, old :%s", newFile.getAbsoluteFile(), origin.getAbsoluteFile()));
        }

        return newFile.getAbsolutePath();
    }
}
