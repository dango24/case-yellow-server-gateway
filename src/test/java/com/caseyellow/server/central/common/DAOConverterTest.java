package com.caseyellow.server.central.common;

import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.SystemInfo;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.persistence.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class DAOConverterTest {

    private static final String HOT = "http://www.hot.net.il/heb/Internet/speed/";

    private static final String FIREFOX = "ftp.mozilla.org/pub/firefox/releases/37.0b1/win32/en-US/Firefox%20Setup%2037.0b1.exe";

    private static final String OS = "WIN10";
    private static final String BROWSER = "CHROME";
    private static final String CONNECTION = "WIFI";
    private static final String IP = "192.1.1.1";

    private static final String TEST_ID = "oren_ha_efes";
    public static final String SPEED_TEST_WEBSITE_IDENTIFIER = "hot";

    private SystemInfo systemInfo;
    private FileDownloadInfo fileDownloadInfo;
    private SpeedTestWebSite speedTestWebSite;
    private ComparisonInfo comparisonInfo;
    private com.caseyellow.server.central.domain.test.model.Test test;

    @Before
    public void setUp() throws Exception {
        systemInfo = new SystemInfo(OS, BROWSER, IP, CONNECTION);
        fileDownloadInfo = new FileDownloadInfo(FIREFOX);
        fileDownloadInfo.setFileDownloadedTimeInMs(102030);
        fileDownloadInfo.setFileDownloadRateKBPerSec(4.5);
        fileDownloadInfo.setFileName("firefox");
        fileDownloadInfo.setFileSizeInBytes(23456);

        speedTestWebSite = new SpeedTestWebSite(HOT, SPEED_TEST_WEBSITE_IDENTIFIER, 123456789);
        comparisonInfo = new ComparisonInfo(speedTestWebSite, fileDownloadInfo);
        test = new com.caseyellow.server.central.domain.test.model.Test.TestBuilder(TEST_ID)
                                                                       .addSpeedTestWebsiteIdentifier(SPEED_TEST_WEBSITE_IDENTIFIER)
                                                                       .addSystemInfo(systemInfo)
                                                                       .addComparisonInfoTests(Arrays.asList(comparisonInfo))
                                                                       .build();
    }

    @Test
    public void convertTestToTestDAO() throws Exception {
        TestDAO testDAO = DAOConverter.convertTestToTestDAO(test);

        assertNotNull(testDAO);
        assertEquals(testDAO.getTestID(), test.getTestID());
        assertEquals(testDAO.getSpeedTestWebsiteIdentifier(), test.getSpeedTestWebsiteIdentifier());
        assertTrue(isComparisonInfoEqualsToComparisonInfoDAO(testDAO.getComparisonInfoDAOTests().get(0), test.getComparisonInfoTests().get(0)));
    }

    @Test
    public void convertSystemInfoToSystemInfoDAO() throws Exception {
        SystemInfoDAO systemInfoDAO = DAOConverter.convertSystemInfoToSystemInfoDAO(systemInfo);
        assertNotNull(systemInfoDAO);
        assertEquals(systemInfoDAO.getBrowser(), systemInfo.getBrowser());
        assertEquals(systemInfoDAO.getConnection(), systemInfo.getConnection());
        assertEquals(systemInfoDAO.getOperatingSystem(), systemInfo.getOperatingSystem());
        assertEquals(systemInfoDAO.getPublicIP(), systemInfo.getPublicIP());
    }

    @Test
    public void convertComparisonInfoToComparisonInfoDAO() throws Exception {
        ComparisonInfoDAO comparisonInfoDAO = DAOConverter.convertComparisonInfoToComparisonInfoDAO(comparisonInfo);
        assertTrue(isComparisonInfoEqualsToComparisonInfoDAO(comparisonInfoDAO, comparisonInfo));
    }

    @Test
    public void convertFileDownloadInfoToFileDownloadInfoDAO() throws Exception {
        FileDownloadInfoDAO fileDownloadInfoDAO = DAOConverter.convertFileDownloadInfoToFileDownloadInfoDAO(fileDownloadInfo);
        assertTrue(isFileDownloadInfoEqualsToFileDownloadInfoDAO(fileDownloadInfoDAO, fileDownloadInfo));
    }

    @Test
    public void convertSpeedTestWebSiteToSpeedTestWebSiteDAO() throws Exception {
        SpeedTestWebSiteDAO speedTestWebSiteDAO = DAOConverter.convertSpeedTestWebSiteToSpeedTestWebSiteDAO(speedTestWebSite);
        assertTrue(isSpeedTestWebSiteEqualsToSpeedTestWebSiteDAO(speedTestWebSiteDAO, speedTestWebSite));
    }

    private boolean isComparisonInfoEqualsToComparisonInfoDAO(ComparisonInfoDAO comparisonInfoDAO, ComparisonInfo comparisonInfo) {
        return isFileDownloadInfoEqualsToFileDownloadInfoDAO(comparisonInfoDAO.getFileDownloadInfoDAO(), comparisonInfo.getFileDownloadInfo()) &&
               isSpeedTestWebSiteEqualsToSpeedTestWebSiteDAO(comparisonInfoDAO.getSpeedTestWebSiteDownloadInfoDAO(), comparisonInfo.getSpeedTestWebSite());
    }

    private boolean isFileDownloadInfoEqualsToFileDownloadInfoDAO(FileDownloadInfoDAO fileDownloadInfoDAO, FileDownloadInfo fileDownloadInfo) {
        return fileDownloadInfoDAO.getFileDownloadedTimeInMs() == fileDownloadInfo.getFileDownloadedTimeInMs() &&
               fileDownloadInfoDAO.getFileDownloadRateKBPerSec() == fileDownloadInfo.getFileDownloadRateKBPerSec() &&
               fileDownloadInfoDAO.getFileName().equals(fileDownloadInfo.getFileName()) &&
               fileDownloadInfoDAO.getFileSizeInBytes() == fileDownloadInfo.getFileSizeInBytes() &&
               fileDownloadInfoDAO.getFileURL().equals(fileDownloadInfo.getFileURL()) &&
               fileDownloadInfoDAO.getStartDownloadingTimestamp() == fileDownloadInfo.getStartDownloadingTimestamp();
    }

    private boolean isSpeedTestWebSiteEqualsToSpeedTestWebSiteDAO(SpeedTestWebSiteDAO speedTestWebSiteDAO, SpeedTestWebSite speedTestWebSite) {
        return speedTestWebSite.getSpeedTestIdentifier().equals(speedTestWebSiteDAO.getSpeedTestIdentifier()) &&
               speedTestWebSite.getStartMeasuringTimestamp() == speedTestWebSiteDAO.getStartMeasuringTimestamp() &&
               speedTestWebSite.getUrlAddress().equals(speedTestWebSiteDAO.getUrlAddress());
    }

}