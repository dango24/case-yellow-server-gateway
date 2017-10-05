package com.caseyellow.server.central.common;

import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.SystemInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.persistence.model.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public interface DAOConverter {

    static TestDAO convertTestToTestDAO(Test test) {
        TestDAO testDAO = new TestDAO.TestBuilder(test.getTestID())
                                     .addSpeedTestWebsite(test.getSpeedTestWebsiteIdentifier())
                                     .addSystemInfo(convertSystemInfoToSystemInfoDAO(test.getSystemInfo()))
                                     .addComparisonInfoTests(convertDtoToModel(DAOConverter::convertComparisonInfoToComparisonInfoDAO, test.getComparisonInfoTests()))
                                     .build();

        return testDAO;
    }

    static SystemInfoDAO convertSystemInfoToSystemInfoDAO(SystemInfo systemInfo) {
        SystemInfoDAO systemInfoDAO = new SystemInfoDAO();

        systemInfoDAO.setBrowser(systemInfo.getBrowser());
        systemInfoDAO.setConnection(systemInfo.getConnection());
        systemInfoDAO.setOperatingSystem(systemInfo.getOperatingSystem());
        systemInfoDAO.setPublicIP(systemInfo.getPublicIP());

        return systemInfoDAO;
    }

    static ComparisonInfoDAO convertComparisonInfoToComparisonInfoDAO(ComparisonInfo comparisonInfo) {
        ComparisonInfoDAO comparisonInfoDAO = new ComparisonInfoDAO();
        comparisonInfoDAO.setFileDownloadInfoDAO(convertFileDownloadInfoToFileDownloadInfoDAO(comparisonInfo.getFileDownloadInfo()));
        comparisonInfoDAO.setSpeedTestWebSiteDownloadInfoDAO(convertSpeedTestWebSiteToSpeedTestWebSiteDAO(comparisonInfo.getSpeedTestWebSite()));

        return comparisonInfoDAO;
    }

    static FileDownloadInfoDAO convertFileDownloadInfoToFileDownloadInfoDAO(FileDownloadInfo fileDownloadInfo) {
        FileDownloadInfoDAO fileDownloadInfoDAO = new FileDownloadInfoDAO.FileDownloadInfoBuilder(fileDownloadInfo.getFileName())
                                                                         .addFileDownloadRateKBPerSec(fileDownloadInfo.getFileDownloadRateKBPerSec())
                                                                         .addFileDownloadedTimeInMs(fileDownloadInfo.getFileDownloadedDurationTimeInMs())
                                                                         .addFileSizeInBytes(fileDownloadInfo.getFileSizeInBytes())
                                                                         .addFileURL(fileDownloadInfo.getFileURL())
                                                                         .addStartDownloadingTime(fileDownloadInfo.getStartDownloadingTimestamp())
                                                                         .build();

        return fileDownloadInfoDAO;
    }

    static SpeedTestWebSiteDAO convertSpeedTestWebSiteToSpeedTestWebSiteDAO(SpeedTestWebSite speedTestWebSite) {
        SpeedTestWebSiteDAO speedTestWebSiteDAO = new SpeedTestWebSiteDAO(speedTestWebSite.getSpeedTestIdentifier());

        speedTestWebSiteDAO.setStartMeasuringTimestamp(speedTestWebSite.getStartMeasuringTimestamp());
        speedTestWebSiteDAO.setUrlAddress(speedTestWebSite.getUrlAddress());

        return speedTestWebSiteDAO;
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

    static <T extends Object, R extends Object> List<R> convertDtoToModel(Function<T, R> convectorFunction,
                                                                          Collection<T> dtoCollection) {
        return convertModelToDto(convectorFunction, dtoCollection);
    }
}
