package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.common.Utils;
import com.caseyellow.server.central.configuration.AWSRegions;
import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import com.caseyellow.server.central.exceptions.FileDownloadInfoException;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.services.storage.FileStorageService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.caseyellow.server.central.common.Utils.convertToMD5;
import static java.lang.Math.min;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Service
public class FileDownloadServiceImp implements FileDownloadService {

    private static final int ESOTERIC_FILE_SIZE = 1600000; // The number of iterations to generate a 30MB file size
    private static final String S3_ESOTERIC_FILES_BUCKET_PREFIX = "esoteric-files";

    @Value("${num_of_comparison_per_test:3}")
    private int numOfComparisonPerTest;

    @Getter @Setter
    private Set<String> esotericFilesLocations;

    private UrlConfig urlConfig;
    private FileStorageService fileStorageService;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;

    @Autowired
    public FileDownloadServiceImp(FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository,
                                  FileStorageService fileStorageService,
                                  UrlConfig urlConfig,
                                  AWSRegions regions) {

        this.urlConfig = urlConfig;
        this.fileStorageService = fileStorageService;
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

        if (nonNull(userName)) {
            nextFileDownloadIdentifiers.addAll(esotericFilesLocations);
        }

        Collections.shuffle(nextFileDownloadIdentifiers);

        return nextFileDownloadIdentifiers.subList(0, min(nextFileDownloadIdentifiers.size(), numOfComparisonPerTest))
                                          .stream()
                                          .map(this::generateFileDownloadProperties)
                                          .collect(toList());
    }

    @Override
    public boolean runClassicTest(String userName) {
        int coinToss = new Random().nextInt(2);

        return coinToss % 2 ==0 ;
    }

    private FileDownloadProperties generateFileDownloadProperties(String identifier) {
        FileDownloadProperties fileDownloadProperties;

        if (esotericFilesLocations.contains(identifier)) {
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
}
