package com.caseyellow.server.central.services.storage;

import com.caseyellow.server.central.exceptions.IORuntimeException;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Profile("prod")
@Service
public class FTPServiceImpl implements FTPService {

    private Logger logger = Logger.getLogger(FTPServiceImpl.class);

    private static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd_MM_yyyy");

    private final static String DOMAIN = "http://yellow.tempurl.co.il/";
    private final static int PORT = 21;
    private final static String SERVER = "yellow.tempurl.co.il";
    private final static String USER = "case@yellow.tempurl.co.il";

    @Value("${ftp_service_key}")
    private String password;

    private FTPClient ftp;

    @PostConstruct
    public void init() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftp.connect(SERVER, PORT);

        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        boolean connectSuccessful = ftp.login(USER, password);

        if (!connectSuccessful) {
            throw new IOException("Failed to connect to FTP Server");
        }

        logger.info("Connect to Ftp service successfully");
    }

    @PreDestroy
    public void close() throws IOException {
        ftp.disconnect();
    }

    private synchronized String getDirectory() throws IOException {
        FTPFile[] directories = ftp.listDirectories();
        String currentDirectory = DAY_FORMAT.format(new Date());

        Optional<String> directoryExists =
            Stream.of(directories)
                  .map(FTPFile::getName)
                  .filter(dirName -> dirName.equals(currentDirectory))
                  .findFirst();

        if (!directoryExists.isPresent()) {
            ftp.makeDirectory(currentDirectory);
            logger.info(String.format("create directory: %s", currentDirectory));
        }

        return currentDirectory;
    }

    @Override
    public String uploadFileToCache(String fileName, File fileToUpload) throws IORuntimeException {
        String filePath = null;

        try (InputStream inputStream = new FileInputStream(fileToUpload)) {

            filePath = String.format("%s/%s", getDirectory(), fileName);
            boolean isUploadSucceed = ftp.storeFile(filePath, inputStream);

            if (!isUploadSucceed) {
                throw new IOException(String.format("Upload failed with code:%s ", ftp.getReplyCode()));
            }

            logger.info(String.format("Uploaded cache file: %s", filePath));

        } catch (IOException e) {
            String errorMessage = String.format("Failed to upload cache file: %s located: %s, cause: %s", filePath, fileToUpload.getAbsolutePath(), e.getMessage());
            logger.error(errorMessage, e);

            throw new IORuntimeException(errorMessage, e);
        }

        return DOMAIN + filePath;
    }
}
