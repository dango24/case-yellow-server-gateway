package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.persistence.repository.FileDownloadInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

        return fileDownloadInfoRepository.groupingFileDownloadInfoByUrl()
                                         .entrySet()
                                         .stream()
                                         .sorted(Map.Entry.comparingByValue())
                                         .map(Map.Entry::getKey)
                                         .collect(toList())
                                         .subList(0, numOfComparisonPerTest);
    }
}
