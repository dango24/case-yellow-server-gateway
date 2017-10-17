package com.caseyellow.server.central.bootstrap;

import com.caseyellow.server.central.common.UrlMapper;
import com.caseyellow.server.central.exceptions.AppBootException;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("prod")
public class ProductionApplicationBoot {

    private UrlMapper urlMapper;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;

    @Autowired
    public ProductionApplicationBoot(UrlMapper urlMapper, FileDownloadInfoCounterRepository fileDownloadInfoRepository, SpeedTestWebSiteCounterRepository speedTestWebSiteRepository) {
        this.urlMapper = urlMapper;
        this.fileDownloadInfoCounterRepository = fileDownloadInfoRepository;
        this.speedTestWebSiteCounterRepository = speedTestWebSiteRepository;
    }

    @PostConstruct
    private void init() throws AppBootException {
        List<String> speedTestNotExistInDB = getSpeedTestNotExistInDB();
        List<String> fileDownloadNotExistInDB = getFileDownloadNotExistInDB();

        if (!speedTestNotExistInDB.isEmpty()) {
            speedTestNotExistInDB.forEach(speedTestWebSiteCounterRepository::addSpeedTestWebSite);
        }

        if (!fileDownloadNotExistInDB.isEmpty()) {
            fileDownloadNotExistInDB.forEach(fileDownloadInfoCounterRepository::addFileDownloadInfo);
        }
    }

    private List<String> getFileDownloadNotExistInDB() {
        Set<String> fileDownloadIdentifiersFromDB = new HashSet<>(fileDownloadInfoCounterRepository.getIdentifiers());
        Set<String> fileDownloadIdentifiersFromResources = urlMapper.getFileDownloadIdentifiers();

        return fileDownloadIdentifiersFromResources.stream()
                                                   .filter(fileIdentifier -> !fileDownloadIdentifiersFromDB.contains(fileIdentifier))
                                                   .collect(Collectors.toList());
    }

    private List<String> getSpeedTestNotExistInDB() {
        Set<String> speedTestIdentifiersFromDB = new HashSet<>(speedTestWebSiteCounterRepository.getIdentifiers());
        Set<String> speedTestIdentifiersFromResources = urlMapper.getSpeedTestIdentifiers();

        return speedTestIdentifiersFromResources.stream()
                                                .filter(fileIdentifier -> !speedTestIdentifiersFromDB.contains(fileIdentifier))
                                                .collect(Collectors.toList());
    }
}
