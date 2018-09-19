package com.caseyellow.server.central.common;

import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.SystemInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.SystemInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.Assert.*;

@ActiveProfiles("dev")
public class DAOConverterTest {

    private static final String HOT = "http://www.hot.net.il/heb/Internet/speed/";
    private static final String FIREFOX = "ftp.mozilla.org/pub/firefox/releases/37.0b1/win32/en-US/Firefox%20Setup%2037.0b1.exe";

    private static final String OS = "WIN10";
    private static final String BROWSER = "CHROME";
    private static final String CONNECTION = "WIFI";
    private static final String IP = "192.1.1.1";

    private static final String TEST_ID = "oren_ha_efes";
    private static final String SPEED_TEST_WEBSITE_IDENTIFIER = "hot";


    private Test test;
    private SystemInfo systemInfo;
    private FileDownloadInfo fileDownloadInfo;
    private SpeedTestWebSite speedTestWebSite;
    private ComparisonInfo comparisonInfo;


    @Before
    public void setUp() throws Exception {
        systemInfo = new SystemInfo(OS, BROWSER, IP, CONNECTION);
        fileDownloadInfo = new FileDownloadInfo(FIREFOX);
        fileDownloadInfo.setFileDownloadedDurationTimeInMs(102030);
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


    @org.junit.Test
    public void convertTestToTestDAO() throws Exception {
        TestDAO testDAO = Converter.convertTestModelToDAO(test);

        assertNotNull(testDAO);
        assertEquals(testDAO.getTestID(), test.getTestID());
        assertEquals(testDAO.getSpeedTestWebsiteIdentifier(), test.getSpeedTestWebsiteIdentifier());
        assertTrue(isComparisonInfoEqualsToComparisonInfoDAO(testDAO.getComparisonInfoDAOTests().get(0), test.getComparisonInfoTests().get(0)));
    }

    @org.junit.Test
    public void convertSystemInfoToSystemInfoDAO() throws Exception {
        SystemInfoDAO systemInfoDAO = Converter.convertSystemInfoModelToDAO(systemInfo);
        assertNotNull(systemInfoDAO);
        assertEquals(systemInfoDAO.getBrowser(), systemInfo.getBrowser());
        assertEquals(systemInfoDAO.getConnection(), systemInfo.getConnection());
        assertEquals(systemInfoDAO.getOperatingSystem(), systemInfo.getOperatingSystem());
        assertEquals(systemInfoDAO.getPublicIP(), systemInfo.getPublicIP());
    }

    @org.junit.Test
    public void convertComparisonInfoToComparisonInfoDAO() throws Exception {
        ComparisonInfoDAO comparisonInfoDAO = Converter.convertComparisonInfoModelToDAO(comparisonInfo);
        assertTrue(isComparisonInfoEqualsToComparisonInfoDAO(comparisonInfoDAO, comparisonInfo));
    }

    @org.junit.Test
    public void convertFileDownloadInfoToFileDownloadInfoDAO() throws Exception {
        FileDownloadInfoDAO fileDownloadInfoDAO = Converter.convertFileDownloadInfoModelToDAO(fileDownloadInfo);
        assertTrue(isFileDownloadInfoEqualsToFileDownloadInfoDAO(fileDownloadInfoDAO, fileDownloadInfo));
    }

    @org.junit.Test
    public void convertSpeedTestWebSiteToSpeedTestWebSiteDAO() throws Exception {
        SpeedTestWebSiteDAO speedTestWebSiteDAO = Converter.convertSpeedTestWebSiteModelToDAO(speedTestWebSite);
        assertTrue(isSpeedTestWebSiteEqualsToSpeedTestWebSiteDAO(speedTestWebSiteDAO, speedTestWebSite));
    }

    private boolean isComparisonInfoEqualsToComparisonInfoDAO(ComparisonInfoDAO comparisonInfoDAO, ComparisonInfo comparisonInfo) {
        return isFileDownloadInfoEqualsToFileDownloadInfoDAO(comparisonInfoDAO.getFileDownloadInfoDAO(), comparisonInfo.getFileDownloadInfo()) &&
               isSpeedTestWebSiteEqualsToSpeedTestWebSiteDAO(comparisonInfoDAO.getSpeedTestWebSiteDAO(), comparisonInfo.getSpeedTestWebSite());
    }

    private boolean isFileDownloadInfoEqualsToFileDownloadInfoDAO(FileDownloadInfoDAO fileDownloadInfoDAO, FileDownloadInfo fileDownloadInfo) {
        return fileDownloadInfoDAO.getFileDownloadedTimeInMs() == fileDownloadInfo.getFileDownloadedDurationTimeInMs() &&
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