package com.caseyellow.server.central.common;

import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.SystemInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.exceptions.ConverterException;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.SystemInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public interface Converter {

    static TestDAO convertTestModelToDAO(Test test) {
        TestDAO testDAO = new TestDAO.TestBuilder(test.getTestID())
                                     .addSpeedTestWebsite(test.getSpeedTestWebsiteIdentifier())
                                     .addSystemInfo(convertSystemInfoModelToDAO(test.getSystemInfo()))
                                     .addComparisonInfoTests(convertDAOToModel(Converter::convertComparisonInfoModelToDAO, test.getComparisonInfoTests()))
                                     .build();

        return testDAO;
    }

    static Test convertTestDAOToModel(TestDAO testDAO) {
        Test test = new Test.TestBuilder(testDAO.getTestID())
                            .addSpeedTestWebsiteIdentifier(testDAO.getSpeedTestWebsiteIdentifier())
                            .addSystemInfo(convertSystemInfoDAOlToModel(testDAO.getSystemInfo()))
                            .addComparisonInfoTests(convertModelToDto(Converter::convertComparisonInfoDAOToModel, testDAO.getComparisonInfoDAOTests()))
                            .build();

        return test;
    }

    static SystemInfoDAO convertSystemInfoModelToDAO(SystemInfo systemInfo) {
        SystemInfoDAO systemInfoDAO = new SystemInfoDAO();

        systemInfoDAO.setBrowser(systemInfo.getBrowser());
        systemInfoDAO.setConnection(systemInfo.getConnection());
        systemInfoDAO.setOperatingSystem(systemInfo.getOperatingSystem());
        systemInfoDAO.setPublicIP(systemInfo.getPublicIP());

        return systemInfoDAO;
    }

    static SystemInfo convertSystemInfoDAOlToModel(SystemInfoDAO systemInfoDAO) {
        SystemInfo systemInfo = new SystemInfo();

        systemInfo.setBrowser(systemInfoDAO.getBrowser());
        systemInfo.setConnection(systemInfoDAO.getConnection());
        systemInfo.setOperatingSystem(systemInfoDAO.getOperatingSystem());
        systemInfo.setPublicIP(systemInfoDAO.getPublicIP());

        return systemInfo;
    }

    static ComparisonInfoDAO convertComparisonInfoModelToDAO(ComparisonInfo comparisonInfo) {
        ComparisonInfoDAO comparisonInfoDAO = new ComparisonInfoDAO();
        comparisonInfoDAO.setFileDownloadInfoDAO(convertFileDownloadInfoModelToDAO(comparisonInfo.getFileDownloadInfo()));
        comparisonInfoDAO.setSpeedTestWebSiteDownloadInfoDAO(convertSpeedTestWebSiteModelToDAO(comparisonInfo.getSpeedTestWebSite()));

        return comparisonInfoDAO;
    }

    static ComparisonInfo convertComparisonInfoDAOToModel(ComparisonInfoDAO comparisonInfoDAO) {
        ComparisonInfo comparisonInfo = new ComparisonInfo();
        comparisonInfo.setFileDownloadInfo(convertFileDownloadInfoDAOToModel(comparisonInfoDAO.getFileDownloadInfoDAO()));
        comparisonInfo.setSpeedTestWebSite(convertSpeedTestWebSiteDAOlToModel(comparisonInfoDAO.getSpeedTestWebSiteDownloadInfoDAO()));

        return comparisonInfo;
    }

    static FileDownloadInfoDAO convertFileDownloadInfoModelToDAO(FileDownloadInfo fileDownloadInfo) {
        if (isNull(fileDownloadInfo)) {
            throw new ConverterException("Failed to convert, fileDownloadInfo is null");
        }
        FileDownloadInfoDAO fileDownloadInfoDAO =
                new FileDownloadInfoDAO.FileDownloadInfoBuilder(fileDownloadInfo.getFileName())
                                       .addFileDownloadRateKBPerSec(fileDownloadInfo.getFileDownloadRateKBPerSec())
                                       .addFileDownloadedTimeInMs(fileDownloadInfo.getFileDownloadedDurationTimeInMs())
                                       .addFileSizeInBytes(fileDownloadInfo.getFileSizeInBytes())
                                       .addFileURL(fileDownloadInfo.getFileURL())
                                       .addStartDownloadingTime(fileDownloadInfo.getStartDownloadingTimestamp())
                                       .build();

        return fileDownloadInfoDAO;
    }

    static FileDownloadInfo convertFileDownloadInfoDAOToModel(FileDownloadInfoDAO fileDownloadInfoDAO) {
        FileDownloadInfo fileDownloadInfo =
                new FileDownloadInfo.FileDownloadInfoBuilder(fileDownloadInfoDAO.getFileName())
                                    .addFileDownloadRateKBPerSec(fileDownloadInfoDAO.getFileDownloadRateKBPerSec())
                                    .addFileDownloadedTimeInMs(fileDownloadInfoDAO.getFileDownloadedTimeInMs())
                                    .addFileSizeInBytes(fileDownloadInfoDAO.getFileSizeInBytes())
                                    .addFileURL(fileDownloadInfoDAO.getFileURL())
                                    .addStartDownloadingTime(fileDownloadInfoDAO.getStartDownloadingTimestamp())
                                    .build();

        return fileDownloadInfo;
    }

    static SpeedTestWebSiteDAO convertSpeedTestWebSiteModelToDAO(SpeedTestWebSite speedTestWebSite) {
        SpeedTestWebSiteDAO speedTestWebSiteDAO = new SpeedTestWebSiteDAO(speedTestWebSite.getSpeedTestIdentifier());

        speedTestWebSiteDAO.setStartMeasuringTimestamp(speedTestWebSite.getStartMeasuringTimestamp());
        speedTestWebSiteDAO.setUrlAddress(speedTestWebSite.getUrlAddress());
        speedTestWebSiteDAO.setKey(speedTestWebSite.getKey());

        return speedTestWebSiteDAO;
    }

    static SpeedTestWebSite convertSpeedTestWebSiteDAOlToModel(SpeedTestWebSiteDAO speedTestWebSiteDAO) {
        SpeedTestWebSite speedTestWebSite = new SpeedTestWebSite(speedTestWebSiteDAO.getSpeedTestIdentifier());

        speedTestWebSite.setStartMeasuringTimestamp(speedTestWebSiteDAO.getStartMeasuringTimestamp());
        speedTestWebSite.setUrlAddress(speedTestWebSiteDAO.getUrlAddress());
        speedTestWebSite.setKey(speedTestWebSiteDAO.getKey());

        return speedTestWebSite;
    }

    static <T extends Object, R extends Object> List<R> convertModelToDto(Function<T, R> convectorFunction,
                                                                          Collection<T> modelCollection) {
        if (isNull(modelCollection) || modelCollection.isEmpty()) {
            return emptyList();
        }

        return modelCollection.stream()
                              .map(convectorFunction)
                              .collect(toList());
    }

    static <T extends Object, R extends Object> List<R> convertDAOToModel(Function<T, R> convectorFunction,
                                                                          Collection<T> daoCollection) {
        return convertModelToDto(convectorFunction, daoCollection);
    }
}
