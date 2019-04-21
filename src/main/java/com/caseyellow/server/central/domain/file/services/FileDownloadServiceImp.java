package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.common.Utils;
import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import com.caseyellow.server.central.exceptions.FileDownloadInfoException;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.services.storage.FileStorageService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static com.caseyellow.server.central.common.Utils.convertToMD5;
import static java.lang.Math.min;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Service
@ConfigurationProperties(prefix = "fileDownload")
public class FileDownloadServiceImp implements FileDownloadService {

    private static final String ESOTERIC_SCRIPT_LOCATION = "/home/ec2-user/case-yellow/scripts";
    private static final String S3_ESOTERIC_FILES_BUCKET_PREFIX = "esoteric-files";

    @Value("${add_extra_file_download}")
    private boolean addExtraFileDownload;

    @Value("${num_of_comparison_per_test:3}")
    private int numOfComparisonPerTest;

    @Getter @Setter
    private List<String> extraIdentifiers;

    @Getter @Setter
    private List<String> esotericFilesLocations;

    private UrlConfig urlConfig;
    private FileStorageService fileStorageService;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;

    @Autowired
    public FileDownloadServiceImp(FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository,
                                  FileStorageService fileStorageService,
                                  UrlConfig urlConfig) {
        this.urlConfig = urlConfig;
        this.fileStorageService = fileStorageService;
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
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

        if (nonNull(userName) && userName.equals("dev2")) {
            nextFileDownloadIdentifiers.addAll(esotericFilesLocations);
        }

        if (addExtraFileDownload) {
            nextFileDownloadIdentifiers.addAll(extraIdentifiers);
        }

        Collections.shuffle(nextFileDownloadIdentifiers);

        return nextFileDownloadIdentifiers.subList(0, min(nextFileDownloadIdentifiers.size(), numOfComparisonPerTest))
                                          .stream()
                                          .map(this::generateFileDownloadProperties)
                                          .collect(toList());
    }

    private FileDownloadProperties generateFileDownloadProperties(String identifier) {
        if (esotericFilesLocations.contains(identifier)) {
            return generateEsotericFileDownloadProperties(identifier);
        } else {
            return urlConfig.getFileDownloadProperties(identifier);
        }
    }

    private FileDownloadProperties generateEsotericFileDownloadProperties(String identifier) {
        File esotericFile = null;

        try {
            String fileSha256 = Utils.executeCommand(String.format("%s/%s", ESOTERIC_SCRIPT_LOCATION, "esoteric"));
            esotericFile = new File(System.getProperty("user.dir"), fileSha256);
            long fileSize = esotericFile.length();
            String bucketName = String.format("%s-%s", S3_ESOTERIC_FILES_BUCKET_PREFIX, identifier);
            String filePath = String.format("%s-%s", identifier, fileSha256);
            String fileUrl = fileStorageService.uploadFileToBucket(identifier, bucketName, filePath, esotericFile);

            return new FileDownloadProperties(identifier, fileUrl, Math.toIntExact(fileSize), convertToMD5(esotericFile));

        } catch (Exception e) {
            Utils.deleteFile(esotericFile);
            throw new FileDownloadInfoException("Failed to generate esoteric file download info: " + e.getMessage(), e);

        } finally {
            Utils.deleteFile(esotericFile);
        }
    }
}
