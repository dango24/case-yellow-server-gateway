package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;

@Service
public class FileDownloadServiceImp implements FileDownloadService {

    @Value("${num_of_comparison_per_test:3}")
    private int numOfComparisonPerTest;

    private UrlConfig urlConfig;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;

    @Autowired
    public FileDownloadServiceImp(FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository, UrlConfig urlConfig) {
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
        this.urlConfig = urlConfig;
    }

    @Override
    public List<FileDownloadProperties> getNextFileDownloadMetaData() {
        List<String> nextFileDownloadIdentifiers;

        if (numOfComparisonPerTest < 0) {
            throw new IllegalArgumentException("numOfComparisonPerTest must be a positive number. received: " + numOfComparisonPerTest);
        } else if (numOfComparisonPerTest == 0) {
            return Collections.emptyList();
        }

        nextFileDownloadIdentifiers = fileDownloadInfoCounterRepository.getActiveIdentifiers();
        Collections.shuffle(nextFileDownloadIdentifiers);

        return nextFileDownloadIdentifiers.subList(0, min(nextFileDownloadIdentifiers.size(), numOfComparisonPerTest))
                                          .stream()
                                          .map(urlConfig::getFileDownloadProperties)
                                          .collect(toList());
    }
}
