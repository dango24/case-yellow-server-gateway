package com.caseyellow.server.central.common;

import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.*;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.exceptions.ConverterException;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.*;
import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromMbpsToKBps;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

public interface Converter {

    static TestDAO convertTestModelToDAO(Test test) {
        if (isNull(test)) {
            throw new ConverterException("Failed to convert, test is null");
        }

        TestDAO testDAO = new TestDAO.TestBuilder(test.getTestID())
                                     .addSpeedTestWebsite(test.getSpeedTestWebsiteIdentifier())
                                     .addComputerIdentifier(test.getComputerIdentifier())
                                     .addUser(test.getUser())
                                     .addISP(test.getIsp())
                                     .addClientVersion(test.getClientVersion())
                                     .addStartTime(test.getStartTime())
                                     .addEndTime(test.getEndTime())
                                     .addClassicTest(test.isClassicTest())
                                     .addSystemInfo(convertSystemInfoModelToDAO(test.getSystemInfo()))
                                     .addComparisonInfoTests(convertDAOToModel(Converter::convertComparisonInfoModelToDAO, test.getComparisonInfoTests()))
                                     .build();
        return testDAO;
    }

    static Test convertTestDAOToModel(TestDAO testDAO) {
        if (isNull(testDAO)) {
            throw new ConverterException("Failed to convert, testDAO is null");
        }

        Test test = new Test.TestBuilder(testDAO.getTestID())
                            .addSpeedTestWebsiteIdentifier(testDAO.getSpeedTestWebsiteIdentifier())
                            .addClientVersion(testDAO.getClientVersion())
                            .addUser(testDAO.getUser())
                            .addISP(testDAO.getIsp())
                            .addClassicTest(isNull(testDAO.getClassicTest()) ? false : testDAO.getClassicTest())
                            .addStartTime(testDAO.getStartTime())
                            .addEndTime(testDAO.getEndTime())
                            .addSystemInfo(convertSystemInfoDAOlToModel(testDAO.getSystemInfo()))
                            .addComparisonInfoTests(convertModelToDto(Converter::convertComparisonInfoDAOToModel, testDAO.getComparisonInfoDAOTests()))
                            .build();

        return test;
    }

    static SystemInfoDAO convertSystemInfoModelToDAO(SystemInfo systemInfo) {
        if (isNull(systemInfo)) {
            throw new ConverterException("Failed to convert, systemInfo is null");
        }

        SystemInfoDAO systemInfoDAO = new SystemInfoDAO();

        systemInfoDAO.setBrowser(systemInfo.getBrowser());
        systemInfoDAO.setConnection(systemInfo.getConnection());
        systemInfoDAO.setOperatingSystem(systemInfo.getOperatingSystem());
        systemInfoDAO.setPublicIP(systemInfo.getPublicIP());

        return systemInfoDAO;
    }

    static SystemInfo convertSystemInfoDAOlToModel(SystemInfoDAO systemInfoDAO) {
        if (isNull(systemInfoDAO)) {
            throw new ConverterException("Failed to convert, systemInfoDAO is null");
        }

        SystemInfo systemInfo = new SystemInfo();

        systemInfo.setBrowser(systemInfoDAO.getBrowser());
        systemInfo.setConnection(systemInfoDAO.getConnection());
        systemInfo.setOperatingSystem(systemInfoDAO.getOperatingSystem());
        systemInfo.setPublicIP(systemInfoDAO.getPublicIP());

        return systemInfo;
    }

    static ComparisonInfoDAO convertComparisonInfoModelToDAO(ComparisonInfo comparisonInfo) {
        if (isNull(comparisonInfo)) {
            throw new ConverterException("Failed to convert, comparisonInfo is null");
        }

        ComparisonInfoDAO comparisonInfoDAO = new ComparisonInfoDAO();
        comparisonInfoDAO.setFileDownloadInfoDAO(convertFileDownloadInfoModelToDAO(comparisonInfo.getFileDownloadInfo()));
        comparisonInfoDAO.setSpeedTestWebSiteDAO(convertSpeedTestWebSiteModelToDAO(comparisonInfo.getSpeedTestWebSite()));

        return comparisonInfoDAO;
    }

    static ComparisonInfo convertComparisonInfoDAOToModel(ComparisonInfoDAO comparisonInfoDAO) {
        if (isNull(comparisonInfoDAO)) {
            throw new ConverterException("Failed to convert, comparisonInfoDAO is null");
        }

        ComparisonInfo comparisonInfo = new ComparisonInfo();
        comparisonInfo.setFileDownloadInfo(convertFileDownloadInfoDAOToModel(comparisonInfoDAO.getFileDownloadInfoDAO()));
        comparisonInfo.setSpeedTestWebSite(convertSpeedTestWebSiteDAOlToModel(comparisonInfoDAO.getSpeedTestWebSiteDAO()));

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
        if (isNull(fileDownloadInfoDAO)) {
            throw new ConverterException("Failed to convert, fileDownloadInfoDAO is null");
        }

        FileDownloadInfo fileDownloadInfo =
                new FileDownloadInfo.FileDownloadInfoBuilder(fileDownloadInfoDAO.getFileName())
                                    .addFileDownloadRateKBPerSec(fileDownloadInfoDAO.getFileDownloadRateKBPerSec())
                                    .addFileDownloadedTimeInMs(fileDownloadInfoDAO.getFileDownloadedTimeInMs())
                                    .addFileSizeInBytes(fileDownloadInfoDAO.getFileSizeInBytes())
                                    .addFileURL(fileDownloadInfoDAO.getFileURL())
                                    .addStartDownloadingTime(fileDownloadInfoDAO.getStartDownloadingTimestamp())
                                    .addHeaders(Collections.emptyMap())
                                    .build();

        return fileDownloadInfo;
    }

    static SpeedTestWebSiteDAO convertSpeedTestWebSiteModelToDAO(SpeedTestWebSite speedTestWebSite) {
        if (isNull(speedTestWebSite)) {
            throw new ConverterException("Failed to convert, speedTestWebSite is null");
        }

        SpeedTestWebSiteDAO speedTestWebSiteDAO = new SpeedTestWebSiteDAO(speedTestWebSite.getSpeedTestIdentifier());

        speedTestWebSiteDAO.setStartMeasuringTimestamp(speedTestWebSite.getStartMeasuringTimestamp());
        speedTestWebSiteDAO.setUrlAddress(speedTestWebSite.getUrlAddress());
        speedTestWebSiteDAO.setS3FileAddress(speedTestWebSite.getPath());

        if (isSpeedTestResultDataExist(speedTestWebSite)) {
            speedTestWebSiteDAO.setDownloadRateInMbps(speedTestWebSite.getDownloadRateInMbps());
            speedTestWebSiteDAO.setAnalyzedState(AnalyzedState.SUCCESS);
        }
        else if (nonNull(speedTestWebSite.getNonFlashResult()) && isCreatable(speedTestWebSite.getNonFlashResult())) {
            speedTestWebSiteDAO.setDownloadRateInMbps(Double.valueOf(speedTestWebSite.getNonFlashResult()));
            speedTestWebSiteDAO.setAnalyzedState(AnalyzedState.SUCCESS);
        }

        return speedTestWebSiteDAO;
    }

    static boolean isSpeedTestResultDataExist(SpeedTestWebSite speedTestWebSite) {
        return nonNull(speedTestWebSite.getDownloadRateInMbps()) &&
               isCreatable(String.valueOf(speedTestWebSite.getDownloadRateInMbps())) &&
               speedTestWebSite.getDownloadRateInMbps() > 0;
    }

    static SpeedTestWebSite convertSpeedTestWebSiteDAOlToModel(SpeedTestWebSiteDAO speedTestWebSiteDAO) {
        if (isNull(speedTestWebSiteDAO)) {
            throw new ConverterException("Failed to convert, speedTestWebSiteDAO is null");
        }
        boolean isAnalyzed = speedTestWebSiteDAO.getAnalyzedState() == AnalyzedState.SUCCESS;
        SpeedTestWebSite speedTestWebSite = new SpeedTestWebSite(speedTestWebSiteDAO.getSpeedTestIdentifier());

        speedTestWebSite.setStartMeasuringTimestamp(speedTestWebSiteDAO.getStartMeasuringTimestamp());
        speedTestWebSite.setUrlAddress(speedTestWebSiteDAO.getUrlAddress());
        speedTestWebSite.setDownloadRateInMbps(speedTestWebSiteDAO.getDownloadRateInMbps());
        speedTestWebSite.setDownloadRateInKBps(calculateDownloadRateFromMbpsToKBps(speedTestWebSiteDAO.getDownloadRateInMbps()));
        speedTestWebSite.setPath(speedTestWebSiteDAO.getS3FileAddress());
        speedTestWebSite.setSucceed(isAnalyzed);

        return speedTestWebSite;
    }

    static FailedTestDAO convertFailedTestModelToDAO(FailedTest failedTest) {
        FailedTestDAO failedTestDAO = new FailedTestDAO();
        failedTestDAO.setIp(failedTest.getIp());
        failedTestDAO.setErrorMessage(failedTest.getErrorMessage());
        failedTestDAO.setPath(failedTest.getPath());
        failedTestDAO.setUser(failedTest.getUser());
        failedTestDAO.setIdentifier(failedTest.getIdentifier());
        failedTestDAO.setClientVersion(failedTest.getClientVersion());

        return failedTestDAO;
    }

    static FailedTest convertFailedTestDAOToModel(FailedTestDAO failedTestDAO) {
        FailedTest failedTest = new FailedTest();
        failedTest.setErrorMessage(failedTestDAO.getErrorMessage());
        failedTest.setIp(failedTestDAO.getIp());
        failedTest.setPath(failedTestDAO.getPath());
        failedTest.setUser(failedTestDAO.getUser());
        failedTest.setIdentifier(failedTestDAO.getIdentifier());
        failedTest.setClientVersion(failedTestDAO.getClientVersion());

        return failedTest;
    }

    static UserDetailsDAO convertUserDetailsModelToDAO(UserDetails userDetails) {
        UserDetailsDAO userDetailsDAO = new UserDetailsDAO();
        userDetailsDAO.setUserName(userDetails.getUserName());
        userDetailsDAO.setInfrastructure(userDetails.getInfrastructure());
        userDetailsDAO.setIsp(userDetails.getIsp());
        userDetailsDAO.setSpeed(userDetails.getSpeed());

        return userDetailsDAO;
    }

    static <T extends Object, R extends Object> List<R> convertModelToDto(Function<T, R> convectorFunction,
                                                                          Collection<T> modelCollection) {
        if (isNull(modelCollection) || modelCollection.isEmpty()) {
            return emptyList();
        } else if (isNull(convectorFunction)) {
            throw new ConverterException("Failed to convert, convectorFunction is null");
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
