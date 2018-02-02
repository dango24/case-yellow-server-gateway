package com.caseyellow.server.central.domain.counter;

import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class CounterManager implements CounterService {

    private UrlConfig urlConfig;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;

    @Autowired
    public CounterManager(UrlConfig urlMapper, SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository, FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository) {
        this.urlConfig = urlMapper;
        this.speedTestWebSiteCounterRepository = speedTestWebSiteCounterRepository;
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
    }

    @Override
    public void addComparisionInfoDetails(String speedTestIdentifier, String fileDownloadIdentifier) {

        if (isNotEmpty(speedTestIdentifier) && urlConfig.isValidSpeedTestIdentifier(speedTestIdentifier)) {
            speedTestWebSiteCounterRepository.addSpeedTestWebSite(speedTestIdentifier);
        }

        if (isNotEmpty(fileDownloadIdentifier) && urlConfig.isValidFileDownloadIdentifier(fileDownloadIdentifier)) {
            fileDownloadInfoCounterRepository.addFileDownloadInfo(fileDownloadIdentifier);
        }
    }

    @Override
    public void decreaseComparisionInfoDetails(String speedTestIdentifier, String fileDownloadIdentifier) {

        if (speedTestWebSiteCounterRepository.findByIdentifier(speedTestIdentifier).getCount() > 0) {
            speedTestWebSiteCounterRepository.reduceSpeedTestWebSite(speedTestIdentifier);
        }

        if (fileDownloadInfoCounterRepository.findByIdentifier(fileDownloadIdentifier).getCount() > 0) {
            fileDownloadInfoCounterRepository.reduceFileDownloadInfo(fileDownloadIdentifier);
        }
    }
}
