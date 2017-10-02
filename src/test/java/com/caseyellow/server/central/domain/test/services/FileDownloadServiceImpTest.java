package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.App;
import com.caseyellow.server.central.domain.file.services.FileDownloadService;
import com.caseyellow.server.central.persistence.model.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.repository.FileDownloadInfoRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes= App.class)
public class FileDownloadServiceImpTest {

    private static final String FIREFOX = "ftp.mozilla.org/pub/firefox/releases/37.0b1/win32/en-US/Firefox%20Setup%2037.0b1.exe";
    private static final String GO = "storage.googleapis.com/golang/go1.7.1.windows-amd64.msi";
    private static final String JAVA_SDK = "sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip";
    private static final String KODI = "http://mirrors.kodi.tv/releases/osx/x86_64/kodi-17.3-Krypton-x86_64.dmg";
    private static final String POSTGRESQL = "http://get.enterprisedb.com/postgresql/postgresql-9.6.0-1-windows-x64.exe";
    private static final String KINECT = "https://download.microsoft.com/download/F/2/D/F2D1012E-3BC6-49C5-B8B3-5ACFF58AF7B8/KinectSDK-v2.0_1409-Setup.exe";
    private static final String ITUNES = " https://secure-appldnld.apple.com/itunes12/031-69284-20160802-7E7B2D20-552B-11E6-B2B9-696CECD541CE/iTunes64Setup.exe";


    @Autowired
    private FileDownloadInfoRepository fileDownloadInfoRepository;

    @Autowired
    private FileDownloadService fileDownloadService;

    @Before
    public void setUp() throws Exception {
        IntStream.range(0, 100).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(FIREFOX)));
        IntStream.range(0, 53).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(GO)));
        IntStream.range(0, 61).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(JAVA_SDK)));
        IntStream.range(0, 10).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(KODI)));
        IntStream.range(0, 15).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(POSTGRESQL)));
        IntStream.range(0, 37).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(KINECT)));
        IntStream.range(0, 28).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(ITUNES)));
    }

    @After
    public void tearDown() throws Exception {
        fileDownloadInfoRepository.deleteAll();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getNextUrlsWithNegativeArgument() throws Exception {
        fileDownloadService.getNextUrls(-1);
    }

    @Test
    public void getNextUrlsWithNonArgument() throws Exception {
        List<String> nextUrls =  fileDownloadService.getNextUrls(0);
        assertThat(nextUrls, empty());
    }

    @Test
    public void getNextUrlsWith1Argument() throws Exception {
        List<String> nextUrls =  fileDownloadService.getNextUrls(1);
        assertThat(nextUrls, containsInAnyOrder(KODI));
    }

    @Test
    public void getNextUrlsWith2Argument() throws Exception {
        List<String> nextUrls =  fileDownloadService.getNextUrls(2);
        assertThat(nextUrls, containsInAnyOrder(KODI, POSTGRESQL));
    }

    @Test
    public void getNextUrlsWith3Argument() throws Exception {
        List<String> nextUrls =  fileDownloadService.getNextUrls(3);
        assertThat(nextUrls, containsInAnyOrder(KODI, POSTGRESQL, ITUNES));
    }

    @Test
    public void getNextUrlsWithBigNumOfComparisonPerTest() throws Exception {
        List<String> nextUrls =  fileDownloadService.getNextUrls(1000000);
        assertThat(nextUrls, containsInAnyOrder(KINECT, KODI, POSTGRESQL, ITUNES, FIREFOX, GO, JAVA_SDK));
    }

}