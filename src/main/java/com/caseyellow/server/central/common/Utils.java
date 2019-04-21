package com.caseyellow.server.central.common;

import com.caseyellow.server.central.exceptions.IORuntimeException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public interface Utils {

    Logger log = Logger.getLogger(Utils.class);

    static String convertToMD5(String str) {
        return DigestUtils.md5Hex(str).toUpperCase();
    }

    static byte[] createImageBase64Encode(String imgPath) throws IOException {
        File imageFile = new File(imgPath);
        byte[] imageBase64Encode = Base64.getEncoder().encode(FileUtils.readFileToByteArray(imageFile));

        return imageBase64Encode;
    }

     static double calculateDownloadRateFromMbpsToKBps(double downloadRateInMbps) {
        double MBPerSec = downloadRateInMbps / 8.0;

        return MBPerSec * Math.pow(2, 10); // Transform to KB
    }

    static double calculateDownloadRateFromKBpsToMbps(double downloadRateInKBps) {
        double MPBerSec = downloadRateInKBps * 8.0;

        return MPBerSec / Math.pow(2, 10); // Transform to Mbps
    }

    static void putMDC(String data) {
        MDC.put("correlation-id", data);
    }

    static void removeMDC() {
        MDC.remove("correlation-id");
    }

    static String convertToMD5(File file)  {

        try (InputStream in = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(IOUtils.toByteArray(in));

            return DatatypeConverter.printHexBinary(md.digest());

        } catch (IOException | NoSuchAlgorithmException e) {
            log.error(String.format("Failed to convert to MD5, error: %s", e.getMessage(), e));
            return "UNKNOWN";
        }

    }

    static String createTempFilename(String fileName) {
        String md5 = convertToMD5(fileName);
        String extension = FilenameUtils.getExtension(fileName);

        if (isNotEmpty(extension)) {
            return md5 + "." + FilenameUtils.getExtension(fileName);
        }

        return md5;
    }

    static String executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);

            return executeInnerCommand(process);

        } catch (IOException e) {
            String errorMessage = String.format("Failed to execute command, cause: ", e.getMessage());
            log.error(errorMessage, e);

            throw new IORuntimeException(errorMessage, e);
        }
    }

    static String executeInnerCommand(Process process) {
        try (InputStream inputStream = process.getInputStream()) {

            return IOUtils.toString(inputStream, UTF_8);

        } catch (Exception e) {
            String errorMessage = String.format("Failed to read input data from inception service, cause: ", e.getMessage());
            log.error(errorMessage, e);

            throw new IORuntimeException(errorMessage, e);
        }
    }

    static File getTmpDir() {
        File rootTmpFile = new File(System.getProperty("java.io.tmpdir"), "case-yellow-tmp-dir");

        if (!rootTmpFile.exists()) {
            rootTmpFile.mkdir();
        } else {
            cleanDirectory(rootTmpFile);
        }

        return rootTmpFile;
    }

    static void archiveDir(String path, String destination) throws ZipException {
        // Initiate ZipFile object with the path/name of the zip file.
        ZipFile zipFile = new ZipFile(destination);

        // Folder to add
        String folderToAdd = path;

        // Initiate Zip Parameters which define various properties such
        // as compression method, etc.
        ZipParameters parameters = new ZipParameters();

        // set compression method to store compression
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

        // Set the compression level
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        // Add folder to the zip file
        zipFile.addFolder(folderToAdd, parameters);

    }

    static void cleanDirectory(File file) {
        try {
            org.apache.commons.io.FileUtils.cleanDirectory(file);
        } catch (IOException e) {
            String errorMessage = String.format("Failed to clean case yellow tmp dir, cause: %s", e.getMessage());
            log.error(errorMessage);
        }
    }

    static void deleteFile(String path) {
        if (nonNull(path)) {
            deleteFile(new File(path));
        }
    }

    static void deleteFile(File file) {
        try {
            if (nonNull(file) && file.exists()) {

                if (file.isDirectory()) {
                    org.apache.commons.io.FileUtils.deleteDirectory(file);
                } else {
                    Files.deleteIfExists(file.toPath());
                }
            }
        } catch (IOException e) {
            log.error(String.format("Failed to delete file: %s", e.getMessage()));
        }
    }

    static File generateLargeFile(int numberOfIterations) {

        File generatedFile = new File(System.getProperty(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()));

        try (BufferedWriter writer = Files.newBufferedWriter(generatedFile.toPath())) {

            IntStream.range(0, numberOfIterations)
                     .mapToLong(index -> ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE))
                     .forEach(num -> {

                        try {
                            writer.append(num + "\n");

                        } catch (IOException e) {
                            throw new IORuntimeException(String.format("Failed to generate file: %s", e.getMessage(), e));
                        }
                     });

        } catch (IOException e) {
            throw new IORuntimeException(String.format("Failed to generate file: %s", e.getMessage(), e));
        }

        return generatedFile;
    }
}
