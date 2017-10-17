package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.SystemInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.TestWrapper;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoRepository;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteRepository;
import com.caseyellow.server.central.persistence.test.repository.TestRepository;
import org.junit.After;
import org.junit.Before;
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
public class TestServiceImplTest {

    private final static int NUM_OF_SUCCEED_TEST = 8;
    private final static int NUM_OF_FAILED_TEST = 5;


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
        IntStream.range(0, NUM_OF_SUCCEED_TEST).forEach(i -> comparisonInfoList.add(new ComparisonInfo(new SpeedTestWebSite(true), new FileDownloadInfo())));
        IntStream.range(0, NUM_OF_FAILED_TEST).forEach(i -> comparisonInfoList.add(new ComparisonInfo(new SpeedTestWebSite(false), new FileDownloadInfo())));
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
        Method removeUnsuccessfulTestsMethod = TestServiceImpl.class.getDeclaredMethod("removeUnsuccessfulTests", TestWrapper.class);
        removeUnsuccessfulTestsMethod.setAccessible(true);

        Test beforeRemoveUnsuccessfulTestsTest = new Test();
        beforeRemoveUnsuccessfulTestsTest.setComparisonInfoTests(comparisonInfoList);

        assertTrue(beforeRemoveUnsuccessfulTestsTest.getComparisonInfoTests().size() == NUM_OF_FAILED_TEST + NUM_OF_SUCCEED_TEST);

        TestWrapper afterRemoveUnsuccessfulTestsTest = (TestWrapper)removeUnsuccessfulTestsMethod.invoke(testService, new TestWrapper(beforeRemoveUnsuccessfulTestsTest));

        assertTrue(afterRemoveUnsuccessfulTestsTest.getTest().getComparisonInfoTests().size() == NUM_OF_SUCCEED_TEST);
    }

   @org.junit.Test
    public void saveTest() throws Exception {
       Test test = new Test.TestBuilder("Esfir")
                           .addComparisonInfoTests(comparisonInfoList)
                           .addSystemInfo(new SystemInfo())
                           .addSpeedTestWebsiteIdentifier("hot")
                           .build();

       assertTrue(testRepository.findAll().isEmpty());

       testService.saveTest(new TestWrapper(test));

       TimeUnit.MILLISECONDS.sleep(700);

       assertNotNull(testRepository.findByTestID("Esfir"));
       assertTrue(speedTestWebSiteRepository.findAll().size() == NUM_OF_SUCCEED_TEST);
       assertTrue(fileDownloadInfoRepository.findAll().size() == NUM_OF_SUCCEED_TEST);
    }

    @org.junit.Test
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


        testService.saveTest(new TestWrapper(test));

        assertTrue(testRepository.findAll().isEmpty());
        assertTrue(speedTestWebSiteRepository.findAll().isEmpty());
        assertTrue(fileDownloadInfoRepository.findAll().isEmpty());
    }
}
