package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.common.Utils;
import com.caseyellow.server.central.configuration.AWSRegions;
import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import com.caseyellow.server.central.exceptions.FileDownloadInfoException;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.services.storage.FTPService;
import com.caseyellow.server.central.services.storage.FileStorageService;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.caseyellow.server.central.common.Utils.convertToMD5;
import static com.caseyellow.server.central.common.Utils.renameFile;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;

@Service
public class FileDownloadServiceImp implements FileDownloadService {

    private Logger logger = Logger.getLogger(FileDownloadServiceImp.class);

    private static final int ESOTERIC_FILE_SIZE = 1600000; // The number of iterations to generate a 30MB file size
    private static final String S3_ESOTERIC_FILES_BUCKET_PREFIX = "esoteric-files";
    public static final String CACHE_IDENTIFIER = "israel_cache";

    @Value("${num_of_comparison_per_test:3}")
    private int numOfComparisonPerTest;

    @Getter @Setter
    private Set<String> esotericFilesLocations;

    private UrlConfig urlConfig;
    private FileStorageService fileStorageService;
    private FTPService ftpService;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;

    @Autowired
    public FileDownloadServiceImp(FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository,
                                  FileStorageService fileStorageService,
                                  FTPService ftpService,
                                  UrlConfig urlConfig,
                                  AWSRegions regions) {

        this.urlConfig = urlConfig;
        this.fileStorageService = fileStorageService;
        this.ftpService = ftpService;
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
        this.esotericFilesLocations = regions.getRegions().keySet();
    }

    @Override
    public List<FileDownloadProperties> getNextFileDownloadMetaData(String userName) {
        List<String> nextFileDownloadIdentifiers;

        if (numOfComparisonPerTest < 0) {
            throw new IllegalArgumentException("numOfComparisonPerTest must be a positive number. received: " + numOfComparisonPerTest);
        } else if (numOfComparisonPerTest == 0) {
            return Collections.emptyList();
        }

        nextFileDownloadIdentifiers = fileDownloadInfoCounterRepository.getActiveIdentifiers();
        nextFileDownloadIdentifiers.add(CACHE_IDENTIFIER);
        nextFileDownloadIdentifiers.addAll(esotericFilesLocations);

        Collections.shuffle(nextFileDownloadIdentifiers);

        return nextFileDownloadIdentifiers.subList(0, min(nextFileDownloadIdentifiers.size(), numOfComparisonPerTest))
                                          .stream()
                                          .map(this::generateFileDownloadProperties)
                                          .collect(toList());
    }

    @Override
    public boolean runClassicTest() {
        int coinToss = new Random().nextInt(4);

        return coinToss != 0;
    }

    private FileDownloadProperties generateFileDownloadProperties(String identifier) {
        FileDownloadProperties fileDownloadProperties;

        if (CACHE_IDENTIFIER.equals(identifier)) {
            fileDownloadProperties = generateCacheFileDownloadProperties();
        } else if (esotericFilesLocations.contains(identifier)) {
            fileDownloadProperties = generateEsotericFileDownloadProperties(identifier);
        } else {
            fileDownloadProperties =  urlConfig.getFileDownloadProperties(identifier);
        }

        fileDownloadProperties.setTimeoutInMin(10);

        return fileDownloadProperties;
    }

    private FileDownloadProperties generateEsotericFileDownloadProperties(String identifier) {
        File esotericFile = null;

        try {
            esotericFile = Utils.generateLargeFile(ESOTERIC_FILE_SIZE);
            long fileSize = esotericFile.length();
            String bucketName = String.format("%s-%s", S3_ESOTERIC_FILES_BUCKET_PREFIX, identifier);
            String md5 = convertToMD5(esotericFile);
            String filePath = String.format("%s-%s", identifier, md5);
            String fileUrl = fileStorageService.uploadFileToBucket(identifier, bucketName, filePath, esotericFile);

            return new FileDownloadProperties(identifier, fileUrl, Math.toIntExact(fileSize), md5, 10);

        } catch (Exception e) {
            throw new FileDownloadInfoException("Failed to generate esoteric file download info: " + e.getMessage(), e);

        } finally {
            Utils.deleteFile(esotericFile);
        }
    }

    private FileDownloadProperties generateCacheFileDownloadProperties() {
        File cacheFile = null;
        File newRenamedFile = null;

        try {
            logger.info("Generate cache file download info");
            cacheFile = Utils.generateLargeFile(ESOTERIC_FILE_SIZE); // Return generated file with UUID
            long fileSize = cacheFile.length();
            String md5 = convertToMD5(cacheFile);
            String fileName = String.format("%s-%s", CACHE_IDENTIFIER, md5);
            newRenamedFile = renameFile(cacheFile, fileName); // Need to rename the file with file MD5 as identifier before upload file to cache server
            String fileUrl = ftpService.uploadFileToCache(fileName, newRenamedFile.getAbsolutePath());

            return new FileDownloadProperties(CACHE_IDENTIFIER, fileUrl, Math.toIntExact(fileSize), md5, 10);

        } catch (Exception e) {
            throw new FileDownloadInfoException("Failed to generate cache file download info: " + e.getMessage(), e);

        } finally {
            Utils.deleteFile(cacheFile);
            Utils.deleteFile(newRenamedFile);
        }
    }
}
