package com.caseyellow.server.central.common;

import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class CounterManager implements CounterService {

    private UrlMapper urlMapper;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;

    @Autowired
    public CounterManager(UrlMapper urlMapper, SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository, FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository) {
        this.urlMapper = urlMapper;
        this.speedTestWebSiteCounterRepository = speedTestWebSiteCounterRepository;
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
    }

    @Override
    public void addComparisionInfoDetails(String speedTestIdentifier, String fileDownloadIdentifier) {

        if (isNotEmpty(speedTestIdentifier) && urlMapper.isValidSpeedTestIdentifier(speedTestIdentifier)) {
            speedTestWebSiteCounterRepository.addSpeedTestWebSite(speedTestIdentifier);
        }

        if (isNotEmpty(fileDownloadIdentifier) && urlMapper.isValidFileDownloadIdentifier(fileDownloadIdentifier)) {
            fileDownloadInfoCounterRepository.addFileDownloadInfo(fileDownloadIdentifier);
        }
    }
}
