package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;
import com.caseyellow.server.central.domain.file.services.FileDownloadService;
import com.caseyellow.server.central.domain.file.services.FileDownloadServiceImp;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class FileDownloadServiceImpTest {

    private static final String FIREFOX = "firefox";
    private static final String GO = "go";
    private static final String JAVA_SDK = "java-sdk";
    private static final String POSTGRESQL = "postgres";
    private static final String KINECT = "kinect";
    private static final String ITUNES = "itunes";

    private final String FIREFOX_URL = "https://ftp.mozilla.org/pub/firefox/releases/37.0b1/win32/en-US/Firefox%20Setup%2037.0b1.exe";
    private final String GO_URL = "https://storage.googleapis.com/golang/go1.7.1.windows-amd64.msi";
    private final String JAVA_SDK_URL = "https://sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip";
    private final String POSTGRESQL_URL = "https://get.enterprisedb.com/postgresql/postgresql-10.0-1-windows-x64.exe";
    private final String KINECT_URL = "https://download.microsoft.com/download/F/2/D/F2D1012E-3BC6-49C5-B8B3-5ACFF58AF7B8/KinectSDK-v2.0_1409-Setup.exe";
    private final String ITUNES_URL = "https://secure-appldnld.apple.com/itunes12/031-69284-20160802-7E7B2D20-552B-11E6-B2B9-696CECD541CE/iTunes64Setup.exe";


    private FileDownloadService fileDownloadService;
    private FileDownloadInfoRepository fileDownloadInfoRepository;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;

    @Autowired
    public void setFileDownloadInfoRepository(FileDownloadInfoRepository fileDownloadInfoRepository) {
        this.fileDownloadInfoRepository = fileDownloadInfoRepository;
    }


    @Autowired
    public void setFileDownloadInfoCounterRepository(FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository) {
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
    }

    @Before
    public void setUp() throws Exception {
        Map<String, FileDownloadProperties> fileDownloadUrls  =new HashMap<>();
        fileDownloadUrls.put(FIREFOX, new FileDownloadProperties(FIREFOX_URL));
        fileDownloadUrls.put(GO, new FileDownloadProperties(GO_URL));
        fileDownloadUrls.put(JAVA_SDK, new FileDownloadProperties(JAVA_SDK_URL));
        fileDownloadUrls.put(POSTGRESQL, new FileDownloadProperties(POSTGRESQL_URL));
        fileDownloadUrls.put(KINECT, new FileDownloadProperties(KINECT_URL));
        fileDownloadUrls.put(ITUNES, new FileDownloadProperties(ITUNES_URL));

        UrlConfig urlMapper = new UrlConfig();

        urlMapper.setFileDownloadProperties(fileDownloadUrls);
        fileDownloadService = new FileDownloadServiceImp(fileDownloadInfoCounterRepository, urlMapper);

        IntStream.range(0, 100).forEach(i -> addFileDownloadInfo(FIREFOX, FIREFOX_URL));
        IntStream.range(0, 53).forEach(i -> addFileDownloadInfo(GO, GO_URL));
        IntStream.range(0, 61).forEach(i -> addFileDownloadInfo(JAVA_SDK, JAVA_SDK_URL));
        IntStream.range(0, 15).forEach(i -> addFileDownloadInfo(POSTGRESQL, POSTGRESQL_URL));
        IntStream.range(0, 37).forEach(i -> addFileDownloadInfo(KINECT, KINECT_URL));
        IntStream.range(0, 28).forEach(i -> addFileDownloadInfo(ITUNES, ITUNES_URL));
    }

    @After
    public void tearDown() throws Exception {
        fileDownloadInfoRepository.deleteAll();
    }


    @Test (expected = IllegalArgumentException.class)
    public void getNextUrlsWithNegativeArgument() throws Exception {
        addNumOfComparisonPerTest(-1);
        fileDownloadService.getNextFileDownloadMetaData();
    }

    @Test
    public void getNextUrlsWithNonArgument() throws Exception {
        List<String> nextUrls  =  getNextUrls(0);
        assertThat(nextUrls, empty());
    }

    @Test
    public void getNextUrlsWithBigNumOfComparisonPerTest() throws Exception {
        List<String> nextUrls =  getNextUrls(1_000_000);
        assertThat(nextUrls, containsInAnyOrder(KINECT_URL, POSTGRESQL_URL, ITUNES_URL, FIREFOX_URL, GO_URL, JAVA_SDK_URL));
    }

    private List<String> getNextUrls(int numOfComparisonPerTest) throws NoSuchFieldException, IllegalAccessException {
        addNumOfComparisonPerTest(numOfComparisonPerTest);
        return fileDownloadService.getNextFileDownloadMetaData()
                                  .stream()
                                  .map(FileDownloadProperties::getUrl)
                                  .collect(Collectors.toList());
    }

    private void addFileDownloadInfo(String identifier, String url) {
        fileDownloadInfoRepository.save(new FileDownloadInfoDAO(identifier, url));
        fileDownloadInfoCounterRepository.addFileDownloadInfo(identifier);
    }

    private void addNumOfComparisonPerTest(int numOfComparisonPerTest) throws NoSuchFieldException, IllegalAccessException {
        Field numOfComparisonPerTestField = FileDownloadServiceImp.class.getDeclaredField("numOfComparisonPerTest");
        numOfComparisonPerTestField.setAccessible(true);
        numOfComparisonPerTestField.set(fileDownloadService, numOfComparisonPerTest);
    }

}