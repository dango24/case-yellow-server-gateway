package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.common.UrlMapper;
import com.caseyellow.server.central.domain.file.model.FileDownloadMetaData;
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

    private UrlMapper urlMapper;
    private FileDownloadInfoRepository fileDownloadInfoRepository;

    @Autowired
    public FileDownloadServiceImp(FileDownloadInfoRepository fileDownloadInfoRepository, UrlMapper urlMapper) {
        this.fileDownloadInfoRepository = fileDownloadInfoRepository;
        this.urlMapper = urlMapper;
    }

    @Override
    public List<FileDownloadMetaData> getNextFileDownloadMetaData(int numOfComparisonPerTest) {
        List<String> nextFileDownloadIdentifiers;

        if (numOfComparisonPerTest < 0) {
            throw new IllegalArgumentException("numOfComparisonPerTest must be a positive number. received: " + numOfComparisonPerTest);
        } else if (numOfComparisonPerTest == 0) {
            return Collections.emptyList();
        }

        nextFileDownloadIdentifiers =
                fileDownloadInfoRepository.groupingFileDownloadInfoByName()
                                          .entrySet()
                                          .stream()
                                          .sorted(Map.Entry.comparingByValue())
                                          .map(Map.Entry::getKey)
                                          .collect(toList());

        return nextFileDownloadIdentifiers.subList(0, min(nextFileDownloadIdentifiers.size(), numOfComparisonPerTest))
                                          .stream()
                                          .map(identifier -> new FileDownloadMetaData(identifier, urlMapper.getFileDownload(identifier)))
                                          .collect(toList());
    }
}
