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
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.Assert.*;

@ActiveProfiles("dev")
public class ModelConverterTest {

    private static final String HOT = "http://www.hot.net.il/heb/Internet/speed/";
    private static final String FIREFOX = "ftp.mozilla.org/pub/firefox/releases/37.0b1/win32/en-US/Firefox%20Setup%2037.0b1.exe";

    private static final String OS = "WIN10";
    private static final String BROWSER = "CHROME";
    private static final String CONNECTION = "WIFI";
    private static final String IP = "192.1.1.1";

    private static final String TEST_ID = "oren_ha_efes";
    private static final String SPEED_TEST_WEBSITE_IDENTIFIER = "hot";


    private TestDAO testDAO;
    private SystemInfoDAO systemInfoDAO;
    private FileDownloadInfoDAO fileDownloadInfoDAO;
    private SpeedTestWebSiteDAO speedTestWebSiteDAO;
    private ComparisonInfoDAO comparisonInfoDAO;


    @Before
    public void setUp() throws Exception {
        systemInfoDAO = new SystemInfoDAO(OS, BROWSER, IP, CONNECTION);
        fileDownloadInfoDAO = new FileDownloadInfoDAO(FIREFOX);
        fileDownloadInfoDAO.setFileDownloadedTimeInMs(102030);
        fileDownloadInfoDAO.setFileDownloadRateKBPerSec(4.5);
        fileDownloadInfoDAO.setFileName("firefox");
        fileDownloadInfoDAO.setFileSizeInBytes(23456);

        speedTestWebSiteDAO = new SpeedTestWebSiteDAO(SPEED_TEST_WEBSITE_IDENTIFIER, HOT, false);
        speedTestWebSiteDAO.setDownloadRateInMbps(123456789.0);

        comparisonInfoDAO = new ComparisonInfoDAO(speedTestWebSiteDAO, fileDownloadInfoDAO);
        testDAO = new TestDAO.TestBuilder(TEST_ID)
                             .addSpeedTestWebsite(SPEED_TEST_WEBSITE_IDENTIFIER)
                             .addSystemInfo(systemInfoDAO)
                             .addComparisonInfoTests(Arrays.asList(comparisonInfoDAO))
                             .build();
    }


    @org.junit.Test
    public void convertTestDAOtoModel() throws Exception {
        Test test = Converter.convertTestDAOToModel(testDAO);

        assertNotNull(test);
        assertEquals(test.getTestID(), testDAO.getTestID());
        assertEquals(test.getSpeedTestWebsiteIdentifier(), testDAO.getSpeedTestWebsiteIdentifier());
        assertTrue(isComparisonInfoModelEqualsToDAO(test.getComparisonInfoTests().get(0), testDAO.getComparisonInfoDAOTests().get(0)));
    }

    @org.junit.Test
    public void convertSystemInfoDAOToModel() throws Exception {
        SystemInfo systemInfo = Converter.convertSystemInfoDAOlToModel(systemInfoDAO);
        assertNotNull(systemInfo);
        assertEquals(systemInfo.getBrowser(), systemInfoDAO.getBrowser());
        assertEquals(systemInfo.getConnection(), systemInfoDAO.getConnection());
        assertEquals(systemInfo.getOperatingSystem(), systemInfoDAO.getOperatingSystem());
        assertEquals(systemInfo.getPublicIP(), systemInfoDAO.getPublicIP());
    }

    @org.junit.Test
    public void convertComparisonInfoDAOToModel() throws Exception {
        ComparisonInfo comparisonInfo = Converter.convertComparisonInfoDAOToModel(comparisonInfoDAO);
        assertTrue(isComparisonInfoModelEqualsToDAO(comparisonInfo, comparisonInfoDAO));
    }

    @org.junit.Test
    public void convertFileDownloadInfoDAOToModel() throws Exception {
        FileDownloadInfo fileDownloadInfo = Converter.convertFileDownloadInfoDAOToModel(fileDownloadInfoDAO);
        assertTrue(isFileDownloadInfoModelEqualsToDAO(fileDownloadInfo, fileDownloadInfoDAO));
    }

    @org.junit.Test
    public void convertSpeedTestWebSiteModelToDAO() throws Exception {
        SpeedTestWebSite speedTestWebSite = Converter.convertSpeedTestWebSiteDAOlToModel(speedTestWebSiteDAO);
        assertTrue(isSpeedTestWebSiteModelEqualsToDAO(speedTestWebSite, speedTestWebSiteDAO));
    }

    private boolean isComparisonInfoModelEqualsToDAO(ComparisonInfo comparisonInfo, ComparisonInfoDAO comparisonInfoDAO) {
        return isFileDownloadInfoModelEqualsToDAO(comparisonInfo.getFileDownloadInfo(), comparisonInfoDAO.getFileDownloadInfoDAO()) &&
               isSpeedTestWebSiteModelEqualsToDAO(comparisonInfo.getSpeedTestWebSite(), comparisonInfoDAO.getSpeedTestWebSiteDAO());
    }

    private boolean isFileDownloadInfoModelEqualsToDAO(FileDownloadInfo fileDownloadInfo, FileDownloadInfoDAO fileDownloadInfoDAO) {
        return fileDownloadInfoDAO.getFileDownloadedTimeInMs() == fileDownloadInfo.getFileDownloadedDurationTimeInMs() &&
               fileDownloadInfoDAO.getFileDownloadRateKBPerSec() == fileDownloadInfo.getFileDownloadRateKBPerSec() &&
               fileDownloadInfoDAO.getFileName().equals(fileDownloadInfo.getFileName()) &&
               fileDownloadInfoDAO.getFileSizeInBytes() == fileDownloadInfo.getFileSizeInBytes() &&
               fileDownloadInfoDAO.getFileURL().equals(fileDownloadInfo.getFileURL()) &&
               fileDownloadInfoDAO.getStartDownloadingTimestamp() == fileDownloadInfo.getStartDownloadingTimestamp();
    }

    private boolean isSpeedTestWebSiteModelEqualsToDAO(SpeedTestWebSite speedTestWebSite, SpeedTestWebSiteDAO speedTestWebSiteDAO) {
        return speedTestWebSite.getSpeedTestIdentifier().equals(speedTestWebSiteDAO.getSpeedTestIdentifier()) &&
               speedTestWebSite.getStartMeasuringTimestamp() == speedTestWebSiteDAO.getStartMeasuringTimestamp() &&
               speedTestWebSite.getUrlAddress().equals(speedTestWebSiteDAO.getUrlAddress());
    }

}