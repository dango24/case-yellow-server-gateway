package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.App;
import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.test.model.ComparisonInfo;
import com.caseyellow.server.central.domain.test.model.SystemInfo;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import com.caseyellow.server.central.persistence.repository.FileDownloadInfoRepository;
import com.caseyellow.server.central.persistence.repository.SpeedTestWebSiteRepository;
import com.caseyellow.server.central.persistence.repository.TestRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= App.class)
public class TestServiceImplTest {

    private final static int NUM_OF_SUCCEED_TEST = 8;
    private final static int NUM_OF_FAILED_TEST = 5;

    @Autowired
    private TestService testService;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Autowired
    private FileDownloadInfoRepository fileDownloadInfoRepository;

    private List<ComparisonInfo> comparisonInfoList;

    @Before
    public void setUp() throws Exception {
        comparisonInfoList = new ArrayList<>();
        IntStream.range(0, NUM_OF_SUCCEED_TEST).forEach(i -> comparisonInfoList.add(new ComparisonInfo(new SpeedTestWebSite(true), new FileDownloadInfo())));
        IntStream.range(0, NUM_OF_FAILED_TEST).forEach(i -> comparisonInfoList.add(new ComparisonInfo(new SpeedTestWebSite(false), new FileDownloadInfo())));
    }

    @After
    public void tearDown() throws Exception {
        testRepository.deleteAll();
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
    public void saveTest() {
       Test test = new Test.TestBuilder("Esfir")
                           .addComparisonInfoTests(comparisonInfoList)
                           .addSystemInfo(new SystemInfo())
                           .addSpeedTestWebsiteIdentifier("hot")
                           .build();

       assertTrue(testRepository.findAll().isEmpty());

       testService.saveTest(test);

       assertTrue(speedTestWebSiteRepository.findAll().size() == NUM_OF_SUCCEED_TEST);
       assertTrue(fileDownloadInfoRepository.findAll().size() == NUM_OF_SUCCEED_TEST);
    }
}