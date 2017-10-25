package com.caseyellow.server.central.common;

import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public interface Validator {

    static boolean validateTest(Test test) {
        if (isNull(test) ||
            isNull(test.getSystemInfo()) ||
            isEmpty(test.getTestID()) ||
            isEmpty(test.getSpeedTestWebsiteIdentifier()) ||
            isNull(test.getComparisonInfoTests())) {

            return false;
        }

        return test.getComparisonInfoTests()
                   .stream()
                   .allMatch(Validator::validateComparisonInfo);
    }

    static boolean validateComparisonInfo(ComparisonInfo comparisonInfo) {
        if (isNull(comparisonInfo) ||
            isNull(comparisonInfo.getSpeedTestWebSite()) ||
            isNull(comparisonInfo.getFileDownloadInfo())) {

            return false;
        }

        return validateSpeedTest(comparisonInfo.getSpeedTestWebSite()) &&
               validateFileDownloadInfo(comparisonInfo.getFileDownloadInfo());
    }

    static boolean validateSpeedTest(SpeedTestWebSite speedTestWebSite) {
        return nonNull(speedTestWebSite) &&
               isNotEmpty(speedTestWebSite.getSpeedTestIdentifier()) &&
               isNotEmpty(speedTestWebSite.getUrlAddress()) &&
               speedTestWebSite.getStartMeasuringTimestamp() > 0;
    }

    static boolean validateFileDownloadInfo(FileDownloadInfo fileDownloadInfo) {
        return nonNull(fileDownloadInfo) &&
               isNotEmpty(fileDownloadInfo.getFileName()) &&
               isNotEmpty(fileDownloadInfo.getFileURL()) &&
               fileDownloadInfo.getFileDownloadRateKBPerSec() > 0 &&
               fileDownloadInfo.getStartDownloadingTimestamp() > 0 &&
               fileDownloadInfo.getFileSizeInBytes() > 0 &&
               fileDownloadInfo.getFileDownloadedDurationTimeInMs() > 0;
    }
}
