package com.caseyellow.server.central.common;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.domain.counter.CounterService;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadCounter;
import com.caseyellow.server.central.persistence.file.repository.FileDownloadInfoCounterRepository;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteCounter;
import com.caseyellow.server.central.persistence.website.repository.SpeedTestWebSiteCounterRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class CounterManagerTest {

    private CounterService counterService;
    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;

    @Autowired
    public void setCounterService(CounterService counterService) {
        this.counterService = counterService;
    }

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

    }

    @After
    public void tearDown() throws Exception {
        fileDownloadInfoCounterRepository.deleteAll();
        speedTestWebSiteCounterRepository.deleteAll();
    }


    @Test
    public void addNullValues() throws Exception {
        counterService.increaseComparisionInfoDetails(null, null);
        assertTrue(fileDownloadInfoCounterRepository.findAll().isEmpty());
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());
    }

    @Test
    public void addFileDownloadFakeValue() throws Exception {
        counterService.increaseComparisionInfoDetails("blabla", null);
        assertTrue(fileDownloadInfoCounterRepository.findAll().isEmpty());
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());
    }

    @Test
    public void addSpeedTestFakeValue() throws Exception {
        counterService.increaseComparisionInfoDetails(null, "blabla");
        assertTrue(fileDownloadInfoCounterRepository.findAll().isEmpty());
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());
    }

    @Test
    public void addFakeValues() throws Exception {
        counterService.increaseComparisionInfoDetails("blabla", "oren_efes");
        assertTrue(fileDownloadInfoCounterRepository.findAll().isEmpty());
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());
    }

    @Test
    public void addSpeedTests() throws Exception {
        IntStream.range(0, 10).forEach(i -> counterService.increaseComparisionInfoDetails("hot", "go"));
        IntStream.range(0, 20).forEach(i -> counterService.increaseComparisionInfoDetails("bezeq", "firefox"));

        assertThat(Arrays.asList("go", "firefox"), containsInAnyOrder(fileDownloadInfoCounterRepository.findAll().stream().map(FileDownloadCounter::getIdentifier).toArray()));
        assertThat(Arrays.asList("hot", "bezeq"), containsInAnyOrder(speedTestWebSiteCounterRepository.findAll().stream().map(SpeedTestWebSiteCounter::getIdentifier).toArray()));

        assertTrue(fileDownloadInfoCounterRepository.groupingFileDownloadInfoByIdentifier().get("go") == 9);
        assertTrue(fileDownloadInfoCounterRepository.groupingFileDownloadInfoByIdentifier().get("firefox") == 19);

        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier("hot").getCount() == 9);
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier("bezeq").getCount() == 19);

        assertTrue(speedTestWebSiteCounterRepository.findMinIdentifier().equals("hot"));
    }
}