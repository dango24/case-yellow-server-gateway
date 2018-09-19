package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.SystemInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoRepository;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteRepository;
import com.caseyellow.server.central.persistence.test.repository.TestRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
@Ignore
public class TestServiceImplTest {

    private final static int NUM_OF_SUCCEED_TEST = 8;
    private final static int NUM_OF_FAILED_TEST = 5;

    private static final String HOT_IDENTIFIER = "hot";
    private static final String HOT_URL = "http://www.hot.net.il/heb/Internet/speed/";

    private static final String GO = "go";
    private static final String GO_URL = "storage.googleapis.com/golang/go1.7.1.windows-amd64.msi";


    private TestService testService;
    private TestRepository testRepository;
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;
    private FileDownloadInfoRepository fileDownloadInfoRepository;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;
    private List<ComparisonInfo> comparisonInfoList;


    @Autowired
    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    @Autowired
    public void setTestRepository(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Autowired
    public void setSpeedTestWebSiteRepository(SpeedTestWebSiteRepository speedTestWebSiteRepository) {
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
    }

    @Autowired
    public void setFileDownloadInfoRepository(FileDownloadInfoRepository fileDownloadInfoRepository) {
        this.fileDownloadInfoRepository = fileDownloadInfoRepository;
    }

    @Autowired
    public void setSpeedTestWebSiteCounterRepository(SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository) {
        this.speedTestWebSiteCounterRepository = speedTestWebSiteCounterRepository;
    }

    @Autowired
    public void setFileDownloadInfoCounterRepository(FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository) {
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
    }

    @Before
    public void setUp() throws Exception {
        comparisonInfoList = new ArrayList<>();
        SpeedTestWebSite speedTestWebSiteSuccess = new SpeedTestWebSite(true, HOT_URL, HOT_IDENTIFIER, 23421);
        SpeedTestWebSite speedTestWebSiteFailure = new SpeedTestWebSite(false, HOT_URL, HOT_IDENTIFIER, 23421);
        FileDownloadInfo fileDownloadInfo = new FileDownloadInfo.FileDownloadInfoBuilder(GO)
                                                                .addFileURL(GO_URL)
                                                                .addFileDownloadRateKBPerSec(32434)
                                                                .addFileDownloadedTimeInMs(543543)
                                                                .addFileSizeInBytes(143543)
                                                                .addStartDownloadingTime(453543)
                                                                .build();

        IntStream.range(0, NUM_OF_SUCCEED_TEST).forEach(i -> comparisonInfoList.add(new ComparisonInfo(speedTestWebSiteSuccess, fileDownloadInfo)));
        IntStream.range(0, NUM_OF_FAILED_TEST).forEach(i -> comparisonInfoList.add(new ComparisonInfo(speedTestWebSiteFailure, fileDownloadInfo)));
    }

    @After
    public void tearDown() throws Exception {
        testRepository.deleteAll();
        speedTestWebSiteRepository.deleteAll();
        fileDownloadInfoRepository.deleteAll();
        speedTestWebSiteCounterRepository.deleteAll();
        fileDownloadInfoCounterRepository.deleteAll();
    }

    @org.junit.Test
    public void removeUnsuccessfulTests() throws Exception{
        Method removeUnsuccessfulTestsMethod = TestServiceImpl.class.getDeclaredMethod("removeUnsuccessfulTests", Test.class);
        removeUnsuccessfulTestsMethod.setAccessible(true);

        Test beforeRemoveUnsuccessfulTestsTest = new Test();
        beforeRemoveUnsuccessfulTestsTest.setComparisonInfoTests(comparisonInfoList);

        assertTrue(beforeRemoveUnsuccessfulTestsTest.getComparisonInfoTests().size() == NUM_OF_FAILED_TEST + NUM_OF_SUCCEED_TEST);

        Test afterRemoveUnsuccessfulTestsTest = (Test)removeUnsuccessfulTestsMethod.invoke(testService, beforeRemoveUnsuccessfulTestsTest);

        assertTrue(afterRemoveUnsuccessfulTestsTest.getComparisonInfoTests().size() == NUM_OF_SUCCEED_TEST);
    }

   @org.junit.Test
    public void saveTest() throws Exception {
       Test test = new Test.TestBuilder("Esfir")
                           .addComparisonInfoTests(comparisonInfoList)
                           .addSystemInfo(new SystemInfo())
                           .addSpeedTestWebsiteIdentifier("hot")
                           .build();

       assertTrue(testRepository.findAll().isEmpty());

       testService.saveTest(test);

       TimeUnit.MILLISECONDS.sleep(700);

       assertNotNull(testRepository.findByTestID("Esfir"));
       assertTrue(speedTestWebSiteRepository.findAll().size() == NUM_OF_SUCCEED_TEST);
       assertTrue(fileDownloadInfoRepository.findAll().size() == NUM_OF_SUCCEED_TEST);
    }

    @org.junit.Test (expected = IllegalArgumentException.class)
    public void saveTestWithNull() {
        List<ComparisonInfo> comparisonInfoList =
            IntStream.range(0, NUM_OF_FAILED_TEST)
                     .mapToObj(i -> new ComparisonInfo(new SpeedTestWebSite(true), null))
                     .collect(Collectors.toList());

        Test test = new Test.TestBuilder("Esfir")
                            .addComparisonInfoTests(comparisonInfoList)
                            .addSystemInfo(new SystemInfo())
                            .addSpeedTestWebsiteIdentifier("hot")
                            .build();


        testService.saveTest(test);
    }

    @org.junit.Test
    public void saveTestWithNullCheckDB() {
        List<ComparisonInfo> comparisonInfoList =
                IntStream.range(0, NUM_OF_FAILED_TEST)
                        .mapToObj(i -> new ComparisonInfo(new SpeedTestWebSite(true), null))
                        .collect(Collectors.toList());

        Test test = new Test.TestBuilder("Esfir")
                .addComparisonInfoTests(comparisonInfoList)
                .addSystemInfo(new SystemInfo())
                .addSpeedTestWebsiteIdentifier("hot")
                .build();

        try {
            testService.saveTest(test);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Test is not valid"));
        }

        assertTrue(testRepository.findAll().isEmpty());
        assertTrue(speedTestWebSiteRepository.findAll().isEmpty());
        assertTrue(fileDownloadInfoRepository.findAll().isEmpty());
    }
}
