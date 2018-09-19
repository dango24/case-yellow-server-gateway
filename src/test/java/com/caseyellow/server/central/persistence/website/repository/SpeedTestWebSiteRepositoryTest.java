package com.caseyellow.server.central.persistence.website.repository;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

/**
 * Created by dango on 9/19/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class SpeedTestWebSiteRepositoryTest {

    private static final int HOT_COUNT = 10;
    private static final int BEZEQ_COUNT = 3;
    private static final int FAST_COUNT = 7;
    private static final int TOTAL_COUNT = FAST_COUNT + HOT_COUNT + BEZEQ_COUNT;

    private static final String HOT_IDENTIFIER = "hot";
    private static final String BEZEQ_IDENTIFIER = "bezeq";
    private static final String FAST_IDENTIFIER = "fast";

    private static final String HOT_URL = "http://www.hot.net.il/heb/Internet/speed/";
    private static final String BEZEQ_URL = "http://www.bezeq.co.il/internetandphone/internet/speedtest/";
    private static final String FAST_URL = "https://www.fast.com";


    private SpeedTestWebSiteRepository speedTestWebSiteRepository;
    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;


    @Autowired
    public void setSpeedTestWebSiteRepository(SpeedTestWebSiteRepository speedTestWebSiteRepository) {
        this.speedTestWebSiteRepository = speedTestWebSiteRepository;
    }

    @Autowired
    public void setSpeedTestWebSiteCounterRepository(SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository) {
        this.speedTestWebSiteCounterRepository = speedTestWebSiteCounterRepository;
    }


    @Before
    public void setUp() throws Exception {
        IntStream.range(0, HOT_COUNT).forEach(i -> addSpeedTestWebSite(new SpeedTestWebSiteDAO(HOT_IDENTIFIER, HOT_URL)));
        IntStream.range(0, BEZEQ_COUNT).forEach(i -> addSpeedTestWebSite(new SpeedTestWebSiteDAO(BEZEQ_IDENTIFIER, BEZEQ_URL)));
        IntStream.range(0, FAST_COUNT).forEach(i -> addSpeedTestWebSite(new SpeedTestWebSiteDAO(FAST_IDENTIFIER, FAST_URL)));
    }

    @After
    public void tearDown() throws Exception {
        speedTestWebSiteCounterRepository.deleteAll();
        speedTestWebSiteRepository.deleteAll();
    }

    @Test
    public void allSpeedTestWebSiteAddedByFindAll() {
        List<SpeedTestWebSiteDAO> allSpeedTest = speedTestWebSiteRepository.findAll();
        assertTrue(allSpeedTest.size() == TOTAL_COUNT);
    }

    @Test
    public void allSpeedTestWebSiteAddedByCount() {
        assertTrue(speedTestWebSiteRepository.count() == TOTAL_COUNT);
    }

    @Test
    public void countBySpeedTestIdentifierHot() {
        assertTrue(speedTestWebSiteRepository.countBySpeedTestIdentifier(HOT_IDENTIFIER) == HOT_COUNT);
    }

    @Test
    public void countBySpeedTestIdentifierBezeq() {
        assertTrue(speedTestWebSiteRepository.countBySpeedTestIdentifier(BEZEQ_IDENTIFIER) == BEZEQ_COUNT);
    }

    @Test
    public void countBySpeedTestIdentifierFast() {
        assertTrue(speedTestWebSiteRepository.countBySpeedTestIdentifier(FAST_IDENTIFIER) == FAST_COUNT);
    }

    @Test
    public void findBySpeedTestIdentifierHot() {
        List<SpeedTestWebSiteDAO> allHotSpeedTest = speedTestWebSiteRepository.findBySpeedTestIdentifier(HOT_IDENTIFIER);
        assertTrue(allHotSpeedTest.size() == HOT_COUNT);
    }

    @Test
    public void findMinSpeedTestWebSite() {
        String minIdentifier = speedTestWebSiteCounterRepository.findMinIdentifier();
        assertEquals(BEZEQ_IDENTIFIER, minIdentifier);
    }

    @Test
    public void findMinAfterAddSpeedTestWebSite() {
        String minIdentifier = speedTestWebSiteCounterRepository.findMinIdentifier();
        assertEquals(BEZEQ_IDENTIFIER, minIdentifier);

        IntStream.range(0, 13).forEach(i -> addSpeedTestWebSite(new SpeedTestWebSiteDAO(BEZEQ_IDENTIFIER)));

        minIdentifier = speedTestWebSiteCounterRepository.findMinIdentifier();
        assertEquals(FAST_IDENTIFIER, minIdentifier);
    }

    @Test
    public void identifierToURLMapperTest() {
        Map<String, String> expectedMapper = new HashMap<>();
        expectedMapper.put(HOT_IDENTIFIER, HOT_URL);
        expectedMapper.put(BEZEQ_IDENTIFIER, BEZEQ_URL);
        expectedMapper.put(FAST_IDENTIFIER, FAST_URL);

        Map<String, String> actualMapper = speedTestWebSiteRepository.getIdentifierToURLMapper();

        assertThat(new HashSet<>(actualMapper.entrySet()), hasItems(expectedMapper.entrySet().toArray()));
    }

    @Test
    public void findByAnalyzedFalse() throws Exception {
        int analyzedTestsCount = 20;
        int unAnalyzedTestsCount = 5;
        
        IntStream.range(0, analyzedTestsCount).forEach(i -> addSpeedTestWebSite(new SpeedTestWebSiteDAO(HOT_IDENTIFIER, HOT_URL, AnalyzedState.SUCCESS)));
        IntStream.range(0, unAnalyzedTestsCount).forEach(i -> addSpeedTestWebSite(new SpeedTestWebSiteDAO(BEZEQ_IDENTIFIER, BEZEQ_URL, AnalyzedState.FAILURE)));

        long unSuccessfulTests = speedTestWebSiteRepository.findByAnalyzedState(AnalyzedState.FAILURE).size() +
                                 speedTestWebSiteRepository.findByAnalyzedState(AnalyzedState.NOT_STARTED).size();

        assertTrue(speedTestWebSiteRepository.count() == TOTAL_COUNT + analyzedTestsCount + unAnalyzedTestsCount);

        assertTrue(unSuccessfulTests == TOTAL_COUNT + unAnalyzedTestsCount);
    }

    @Test
    public void findByAnalyzedTrue() throws Exception {
        int analyzedTestsCount = 20;
        int unAnalyzedTestsCount = 5;

        IntStream.range(0, analyzedTestsCount).forEach(i -> addSpeedTestWebSite(new SpeedTestWebSiteDAO(HOT_IDENTIFIER, HOT_URL, AnalyzedState.SUCCESS)));
        IntStream.range(0, unAnalyzedTestsCount).forEach(i -> addSpeedTestWebSite(new SpeedTestWebSiteDAO(BEZEQ_IDENTIFIER, BEZEQ_URL)));

        assertTrue(speedTestWebSiteRepository.count() == TOTAL_COUNT + analyzedTestsCount + unAnalyzedTestsCount);

        assertTrue(speedTestWebSiteRepository.findByAnalyzedState(AnalyzedState.SUCCESS).size() == analyzedTestsCount);
    }

    private void addSpeedTestWebSite(SpeedTestWebSiteDAO speedTestWebSiteDAO) {
        speedTestWebSiteRepository.save(speedTestWebSiteDAO);
        speedTestWebSiteCounterRepository.addSpeedTestWebSite(speedTestWebSiteDAO.getSpeedTestIdentifier());
    }
}