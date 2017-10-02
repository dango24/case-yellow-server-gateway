package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.persistence.repository.FileDownloadInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;

@Service
public class FileDownloadServiceImp implements FileDownloadService {

    private FileDownloadInfoRepository fileDownloadInfoRepository;

    @Autowired
    public FileDownloadServiceImp(FileDownloadInfoRepository fileDownloadInfoRepository) {
        this.fileDownloadInfoRepository = fileDownloadInfoRepository;
    }

    @Override
    public List<String> getNextUrls(int numOfComparisonPerTest) {
        List<String> nextUrls;

        if (numOfComparisonPerTest < 0) {
            throw new IllegalArgumentException("numOfComparisonPerTest must be a positive number.");
        } else if (numOfComparisonPerTest == 0) {
            return Collections.emptyList();
        }

        nextUrls =  fileDownloadInfoRepository.groupingFileDownloadInfoByUrl()
                                              .entrySet()
                                              .stream()
                                              .sorted(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey)
                                              .collect(toList());

        return nextUrls.subList(0, min(nextUrls.size(), numOfComparisonPerTest));
    }
}
