package com.caseyellow.server.central.bootstrap;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.common.UrlMapper;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadCounter;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteCounter;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class ProductionAppBootTest {

    private static final String HOT_IDENTIFIER = "HOT";
    private static final String ATNT_IDENTIFIER = "atnt";
    private static final String BEZEQ_IDENTIFIER = "bezeq";
    private static final String FAST_IDENTIFIER = "fast";
    private static final String OOKLA_IDENTIFIER = "ookla";
    private static final String SPEED_OF_IDENTIFIER = "speedof";

    private static final String FIREFOX_IDENTIFIER = "firefox";
    private static final String GO_IDENTIFIER = "go";
    private static final String JAVA_SDK_IDENTIFIER = "java-sdk";
    private static final String POSTGRESQL_IDENTIFIER = "postgres";
    private static final String KINECT_IDENTIFIER = "kinect";
    private static final String ITUNES_IDENTIFIER = "itunes";


    private ProductionApplicationBoot commonAppBoot;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;


    @Autowired
    public void setFileDownloadInfoCounterRepository(FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository) {
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
    }

    @Autowired
    public void setSpeedTestWebSiteCounterRepository(SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository) {
        this.speedTestWebSiteCounterRepository = speedTestWebSiteCounterRepository;
    }


    @Before
    public void setUp() throws Exception {
        List<String> fileDownloadsList = Arrays.asList(FIREFOX_IDENTIFIER, GO_IDENTIFIER, JAVA_SDK_IDENTIFIER, POSTGRESQL_IDENTIFIER, KINECT_IDENTIFIER, ITUNES_IDENTIFIER);
        List<String> speedTestList = Arrays.asList(HOT_IDENTIFIER, ATNT_IDENTIFIER, BEZEQ_IDENTIFIER, FAST_IDENTIFIER, OOKLA_IDENTIFIER, SPEED_OF_IDENTIFIER);
        UrlMapper urlMapper = mock(UrlMapper.class);
        when(urlMapper.getFileDownloadIdentifiers()).thenReturn(new HashSet<>(fileDownloadsList));
        when(urlMapper.getSpeedTestIdentifiers()).thenReturn(new HashSet<>(speedTestList));

        commonAppBoot = new ProductionApplicationBoot(urlMapper, fileDownloadInfoCounterRepository, speedTestWebSiteCounterRepository);
    }

    @After
    public void tearDown() throws Exception {
        fileDownloadInfoCounterRepository.deleteAll();
        speedTestWebSiteCounterRepository.deleteAll();
    }


    @Test
    public void fileDownloadNoneMatchesTest() throws Exception{

        Method getFileDownloadNotExistInDBMethod = ProductionApplicationBoot.class.getDeclaredMethod("getFileDownloadNotExistInDB");
        getFileDownloadNotExistInDBMethod.setAccessible(true);

        List<String> fileDownloadNotExistInDB = (List<String>)getFileDownloadNotExistInDBMethod.invoke(commonAppBoot);

        assertThat(Arrays.asList(FIREFOX_IDENTIFIER, GO_IDENTIFIER, JAVA_SDK_IDENTIFIER, POSTGRESQL_IDENTIFIER, KINECT_IDENTIFIER, ITUNES_IDENTIFIER),
                   containsInAnyOrder(fileDownloadNotExistInDB.toArray()));
    }

    @Test
    public void fileDownloadSomeMatchesTest() throws Exception{
        IntStream.range(0, 5).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(GO_IDENTIFIER));
        IntStream.range(0, 4).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(POSTGRESQL_IDENTIFIER));
        IntStream.range(0, 3).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(ITUNES_IDENTIFIER));

        Method getFileDownloadNotExistInDBMethod = ProductionApplicationBoot.class.getDeclaredMethod("getFileDownloadNotExistInDB");
        getFileDownloadNotExistInDBMethod.setAccessible(true);

        List<String> fileDownloadNotExistInDB = (List<String>)getFileDownloadNotExistInDBMethod.invoke(commonAppBoot);

        assertThat(Arrays.asList(FIREFOX_IDENTIFIER, KINECT_IDENTIFIER, JAVA_SDK_IDENTIFIER), containsInAnyOrder(fileDownloadNotExistInDB.toArray()));
    }

    @Test
    public void fileDownloadAllMatchesTest() throws Exception{
        IntStream.range(0, 5).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(GO_IDENTIFIER));
        IntStream.range(0, 4).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(POSTGRESQL_IDENTIFIER));
        IntStream.range(0, 3).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(ITUNES_IDENTIFIER));
        IntStream.range(0, 2).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(JAVA_SDK_IDENTIFIER));
        IntStream.range(0, 11).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(FIREFOX_IDENTIFIER));
        IntStream.range(0, 9).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(KINECT_IDENTIFIER));

        Method getFileDownloadNotExistInDBMethod = ProductionApplicationBoot.class.getDeclaredMethod("getFileDownloadNotExistInDB");
        getFileDownloadNotExistInDBMethod.setAccessible(true);

        List<String> fileDownloadNotExistInDB = (List<String>)getFileDownloadNotExistInDBMethod.invoke(commonAppBoot);

        assertThat(Collections.emptyList(), containsInAnyOrder(fileDownloadNotExistInDB.toArray()));
    }

    @Test
    public void speedTestNoneMatchesTest() throws Exception{

        Method getSpeedTestNotExistInDBMethod = ProductionApplicationBoot.class.getDeclaredMethod("getSpeedTestNotExistInDB");
        getSpeedTestNotExistInDBMethod.setAccessible(true);

        List<String> speedTestNotExistInDB = (List<String>)getSpeedTestNotExistInDBMethod.invoke(commonAppBoot);

        assertThat(Arrays.asList(FAST_IDENTIFIER, SPEED_OF_IDENTIFIER, ATNT_IDENTIFIER, HOT_IDENTIFIER, BEZEQ_IDENTIFIER, OOKLA_IDENTIFIER),
                   containsInAnyOrder(speedTestNotExistInDB.toArray()));
    }

    @Test
    public void speedTestSomeMatchesTest() throws Exception{
        IntStream.range(0, 2).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(HOT_IDENTIFIER));
        IntStream.range(0, 7).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(BEZEQ_IDENTIFIER));
        IntStream.range(0, 5).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(OOKLA_IDENTIFIER));

        Method getSpeedTestNotExistInDBMethod = ProductionApplicationBoot.class.getDeclaredMethod("getSpeedTestNotExistInDB");
        getSpeedTestNotExistInDBMethod.setAccessible(true);

        List<String> speedTestNotExistInDB = (List<String>)getSpeedTestNotExistInDBMethod.invoke(commonAppBoot);

        assertThat(Arrays.asList(FAST_IDENTIFIER, SPEED_OF_IDENTIFIER, ATNT_IDENTIFIER), containsInAnyOrder(speedTestNotExistInDB.toArray()));
    }

    @Test
    public void speedTestAllMatchesTest() throws Exception{
        IntStream.range(0, 2).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(HOT_IDENTIFIER));
        IntStream.range(0, 7).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(BEZEQ_IDENTIFIER));
        IntStream.range(0, 5).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(OOKLA_IDENTIFIER));
        IntStream.range(0, 6).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(FAST_IDENTIFIER));
        IntStream.range(0, 8).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(SPEED_OF_IDENTIFIER));
        IntStream.range(0, 1).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(ATNT_IDENTIFIER));

        Method getSpeedTestNotExistInDBMethod = ProductionApplicationBoot.class.getDeclaredMethod("getSpeedTestNotExistInDB");
        getSpeedTestNotExistInDBMethod.setAccessible(true);

        List<String> speedTestNotExistInDB = (List<String>)getSpeedTestNotExistInDBMethod.invoke(commonAppBoot);

        assertThat(Collections.emptyList(), containsInAnyOrder(speedTestNotExistInDB.toArray()));
    }

    @Test
    public void initNoneTest() throws Exception {
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());
        assertTrue(fileDownloadInfoCounterRepository.findAll().isEmpty());

        Method initMethod = ProductionApplicationBoot.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(commonAppBoot);

        String[] actualSpeedTestIdentifiers =
                speedTestWebSiteCounterRepository.findAll()
                                                 .stream()
                                                 .map(SpeedTestWebSiteCounter::getIdentifier)
                                                 .toArray(size -> new String[size]);

        String[] actualFileDownloadIdentifiers =
                fileDownloadInfoCounterRepository.findAll()
                                                 .stream()
                                                 .map(FileDownloadCounter::getIdentifier)
                                                 .toArray(size -> new String[size]);

        assertThat(Arrays.asList(FAST_IDENTIFIER, SPEED_OF_IDENTIFIER, ATNT_IDENTIFIER, HOT_IDENTIFIER, BEZEQ_IDENTIFIER, OOKLA_IDENTIFIER), containsInAnyOrder(actualSpeedTestIdentifiers));
        assertThat(Arrays.asList(FIREFOX_IDENTIFIER, GO_IDENTIFIER, JAVA_SDK_IDENTIFIER, POSTGRESQL_IDENTIFIER, KINECT_IDENTIFIER, ITUNES_IDENTIFIER), containsInAnyOrder(actualFileDownloadIdentifiers));
    }

    @Test
    public void initSomeRecordsTest() throws Exception {
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());
        assertTrue(fileDownloadInfoCounterRepository.findAll().isEmpty());

        IntStream.range(0, 2).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(HOT_IDENTIFIER));
        IntStream.range(0, 7).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(BEZEQ_IDENTIFIER));
        IntStream.range(0, 5).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(OOKLA_IDENTIFIER));

        IntStream.range(0, 4).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(POSTGRESQL_IDENTIFIER));
        IntStream.range(0, 3).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(ITUNES_IDENTIFIER));
        IntStream.range(0, 2).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(JAVA_SDK_IDENTIFIER));
        IntStream.range(0, 11).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(FIREFOX_IDENTIFIER));

        Method initMethod = ProductionApplicationBoot.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(commonAppBoot);

        String[] actualSpeedTestIdentifiers =
                speedTestWebSiteCounterRepository.findAll()
                                                 .stream()
                                                 .map(SpeedTestWebSiteCounter::getIdentifier)
                                                 .toArray(size -> new String[size]);

        String[] actualFileDownloadIdentifiers =
                fileDownloadInfoCounterRepository.findAll()
                                                 .stream()
                                                 .map(FileDownloadCounter::getIdentifier)
                                                 .toArray(size -> new String[size]);

        assertThat(Arrays.asList(FAST_IDENTIFIER, SPEED_OF_IDENTIFIER, ATNT_IDENTIFIER, HOT_IDENTIFIER, BEZEQ_IDENTIFIER, OOKLA_IDENTIFIER), containsInAnyOrder(actualSpeedTestIdentifiers));
        assertThat(Arrays.asList(FIREFOX_IDENTIFIER, GO_IDENTIFIER, JAVA_SDK_IDENTIFIER, POSTGRESQL_IDENTIFIER, KINECT_IDENTIFIER, ITUNES_IDENTIFIER), containsInAnyOrder(actualFileDownloadIdentifiers));
    }

    @Test
    public void initAllRecordsTest() throws Exception {
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());
        assertTrue(fileDownloadInfoCounterRepository.findAll().isEmpty());

        IntStream.range(0, 2).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(HOT_IDENTIFIER));
        IntStream.range(0, 7).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(BEZEQ_IDENTIFIER));
        IntStream.range(0, 5).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(OOKLA_IDENTIFIER));
        IntStream.range(0, 6).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(FAST_IDENTIFIER));
        IntStream.range(0, 8).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(SPEED_OF_IDENTIFIER));
        IntStream.range(0, 1).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(ATNT_IDENTIFIER));

        IntStream.range(0, 5).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(GO_IDENTIFIER));
        IntStream.range(0, 4).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(POSTGRESQL_IDENTIFIER));
        IntStream.range(0, 3).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(ITUNES_IDENTIFIER));
        IntStream.range(0, 2).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(JAVA_SDK_IDENTIFIER));
        IntStream.range(0, 11).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(FIREFOX_IDENTIFIER));
        IntStream.range(0, 9).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(KINECT_IDENTIFIER));


        Method initMethod = ProductionApplicationBoot.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(commonAppBoot);

        String[] actualSpeedTestIdentifiers =
                speedTestWebSiteCounterRepository.findAll()
                                                 .stream()
                                                 .map(SpeedTestWebSiteCounter::getIdentifier)
                                                 .toArray(size -> new String[size]);

        String[] actualFileDownloadIdentifiers =
                fileDownloadInfoCounterRepository.findAll()
                                                 .stream()
                                                 .map(FileDownloadCounter::getIdentifier)
                                                 .toArray(size -> new String[size]);

        assertThat(Arrays.asList(FAST_IDENTIFIER, SPEED_OF_IDENTIFIER, ATNT_IDENTIFIER, HOT_IDENTIFIER, BEZEQ_IDENTIFIER, OOKLA_IDENTIFIER), containsInAnyOrder(actualSpeedTestIdentifiers));
        assertThat(Arrays.asList(FIREFOX_IDENTIFIER, GO_IDENTIFIER, JAVA_SDK_IDENTIFIER, POSTGRESQL_IDENTIFIER, KINECT_IDENTIFIER, ITUNES_IDENTIFIER), containsInAnyOrder(actualFileDownloadIdentifiers));
    }

}