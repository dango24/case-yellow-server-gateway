package com.caseyellow.server.central.bootstrap;

import com.caseyellow.server.central.common.UrlMapper;
import com.caseyellow.server.central.exceptions.AppBootException;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoRepository;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteRepository;
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
    private FileDownloadInfoRepository fileDownloadInfoRepository;
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    public ProductionApplicationBoot(UrlMapper urlMapper, FileDownloadInfoRepository fileDownloadInfoRepository, SpeedTestWebSiteRepository speedTestWebSiteRepository) {
        this.urlMapper = urlMapper;
        this.fileDownloadInfoRepository = fileDownloadInfoRepository;
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
    }

    @PostConstruct
    private void init() throws AppBootException {
        List<String> speedTestNotExistInDB = getSpeedTestNotExistInDB();
        List<String> fileDownloadNotExistInDB = getFileDownloadNotExistInDB();

        if (!speedTestNotExistInDB.isEmpty()) {
            speedTestNotExistInDB.forEach(speedTest -> speedTestWebSiteRepository.save(new SpeedTestWebSiteDAO(speedTest)));
        }

        if (!fileDownloadNotExistInDB.isEmpty()) {
            fileDownloadNotExistInDB.forEach(fileDownload -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(fileDownload, null)));
        }
    }

    private List<String> getFileDownloadNotExistInDB() {
        Set<String> fileDownloadIdentifiersFromDB = new HashSet<>(fileDownloadInfoRepository.getAllFileIdentifiers());
        Set<String> fileDownloadIdentifiersFromResources = urlMapper.getFileDownloadIdentifiers();

        return fileDownloadIdentifiersFromResources.stream()
                                                   .filter(fileIdentifier -> !fileDownloadIdentifiersFromDB.contains(fileIdentifier))
                                                   .collect(Collectors.toList());
    }

    private List<String> getSpeedTestNotExistInDB() {
        Set<String> speedTestIdentifiersFromDB = new HashSet<>(speedTestWebSiteRepository.getAllSpeedTestIdentifiers());
        Set<String> speedTestIdentifiersFromResources = urlMapper.getSpeedTestIdentifiers();

        return speedTestIdentifiersFromResources.stream()
                                                .filter(fileIdentifier -> !speedTestIdentifiersFromDB.contains(fileIdentifier))
                                                .collect(Collectors.toList());
    }
}
