package com.caseyellow.server.central.bootstrap;

import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.exceptions.AppBootException;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadCounter;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteCounter;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Component
@Profile("prod")
public class ApplicationBoot {

    private UrlConfig urlMapper;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;

    @Autowired
    public ApplicationBoot(UrlConfig urlMapper, FileDownloadInfoCounterRepository fileDownloadInfoRepository, SpeedTestWebSiteCounterRepository speedTestWebSiteRepository) {
        this.urlMapper = urlMapper;
        this.fileDownloadInfoCounterRepository = fileDownloadInfoRepository;
        this.speedTestWebSiteCounterRepository = speedTestWebSiteRepository;
    }

    @PostConstruct
    @Transactional
    public void init() throws AppBootException {
        addNewIdentifiers();
        disableNonActiveIdentifiers();
        enableActiveIdentifiers();
    }

    private void addNewIdentifiers() {
        List<String> speedTestNotExistInDB = getSpeedTestNotExistInDB();
        List<String> fileDownloadNotExistInDB = getFileDownloadNotExistInDB();

        if (!speedTestNotExistInDB.isEmpty()) {
            speedTestNotExistInDB.forEach(speedTestWebSiteCounterRepository::addSpeedTestWebSite);
        }

        if (!fileDownloadNotExistInDB.isEmpty()) {
            fileDownloadNotExistInDB.forEach(fileDownloadInfoCounterRepository::addFileDownloadInfo);
        }
    }

    private void enableActiveIdentifiers() {
        activeFileDownloadInfo();
        activeSpeedTestWebSite();
    }

    private void activeFileDownloadInfo() {
        Set<String> fileDownloadIdentifiersFromResources = urlMapper.getFileDownloadIdentifiers();

        fileDownloadInfoCounterRepository.findAll()
                                         .stream()
                                         .map(FileDownloadCounter::getIdentifier)
                                         .filter(fileDownloadIdentifiersFromResources::contains)
                                         .forEach(fileDownloadInfoCounterRepository::activeFileDownloadInfo);
    }

    private void activeSpeedTestWebSite() {
        Set<String> speedTestIdentifiersFromResources = urlMapper.getSpeedTestIdentifiers();

        speedTestWebSiteCounterRepository.findAll()
                                         .stream()
                                         .map(SpeedTestWebSiteCounter::getIdentifier)
                                         .filter(speedTestIdentifiersFromResources::contains)
                                         .forEach(speedTestWebSiteCounterRepository::activeSpeedTestWebSite);
    }

    private List<String> getFileDownloadNotExistInDB() {
        Set<String> fileDownloadIdentifiersFromDB = new HashSet<>(fileDownloadInfoCounterRepository.getIdentifiers());
        Set<String> fileDownloadIdentifiersFromResources = urlMapper.getFileDownloadIdentifiers();

        return fileDownloadIdentifiersFromResources.stream()
                                                   .filter(fileIdentifier -> !fileDownloadIdentifiersFromDB.contains(fileIdentifier))
                                                   .collect(toList());
    }

    private List<String> getSpeedTestNotExistInDB() {
        Set<String> speedTestIdentifiersFromDB = new HashSet<>(speedTestWebSiteCounterRepository.getIdentifiers());
        Set<String> speedTestIdentifiersFromResources = urlMapper.getSpeedTestIdentifiers();

        return speedTestIdentifiersFromResources.stream()
                                                .filter(fileIdentifier -> !speedTestIdentifiersFromDB.contains(fileIdentifier))
                                                .collect(toList());
    }

    private void disableNonActiveIdentifiers() {
        List<String> fileDownloadIdentifiersToDeactivate =
                fileDownloadInfoCounterRepository.getIdentifiers()
                                                 .stream()
                                                 .filter(identifier -> !urlMapper.getFileDownloadIdentifiers().contains(identifier))
                                                 .collect(toList());

        List<String> speedTestIdentifiersToDeactivate =
                speedTestWebSiteCounterRepository.getIdentifiers()
                                                 .stream()
                                                 .filter(identifier -> !urlMapper.getSpeedTestIdentifiers().contains(identifier))
                                                 .collect(toList());

        fileDownloadIdentifiersToDeactivate.forEach(fileDownloadInfoCounterRepository::deActiveFileDownloadInfo);
        speedTestIdentifiersToDeactivate.forEach(speedTestWebSiteCounterRepository::deActiveSpeedTestWebSite);
    }
}
