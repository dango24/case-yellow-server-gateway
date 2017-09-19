package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.App;
import com.caseyellow.server.central.persistence.test.model.SpeedTestWebSiteDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by dango on 9/19/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= App.class)
public class SpeedTestWebSiteRepositoryTest {

    private static final String HOT_IDENTIFIER = "hot";
    private static final String BEZEQ_IDENTIFIER = "bezeq";
    private static final String FAST_IDENTIFIER = "fast";

    @Autowired
    private SpeedTestWebSiteRepository speedTestWebSiteRepository;

    @Before
    public void setUp() throws Exception {
        IntStream.range(0, 10).forEach(i -> speedTestWebSiteRepository.save(new SpeedTestWebSiteDAO(HOT_IDENTIFIER)));
        IntStream.range(0, 3).forEach(i -> speedTestWebSiteRepository.save(new SpeedTestWebSiteDAO(BEZEQ_IDENTIFIER)));
        IntStream.range(0, 7).forEach(i -> speedTestWebSiteRepository.save(new SpeedTestWebSiteDAO(FAST_IDENTIFIER)));
    }

    @After
    public void tearDown() throws Exception {
        speedTestWebSiteRepository.deleteAll();
    }

    @Test
    public void allSpeedTestWebSiteAddedByFindAll() {
        List<SpeedTestWebSiteDAO> allSpeedTest = speedTestWebSiteRepository.findAll();
        assertTrue(allSpeedTest.size() == 20);
    }

    @Test
    public void allSpeedTestWebSiteAddedByCount() {
        assertTrue(speedTestWebSiteRepository.count() == 20);
    }

    @Test
    public void countBySpeedTestIdentifierHot() {
        assertTrue(speedTestWebSiteRepository.countBySpeedTestIdentifier(HOT_IDENTIFIER) == 10);
    }

    @Test
    public void countBySpeedTestIdentifierBezeq() {
        assertTrue(speedTestWebSiteRepository.countBySpeedTestIdentifier(BEZEQ_IDENTIFIER) == 3);
    }

    @Test
    public void countBySpeedTestIdentifierFast() {
        assertTrue(speedTestWebSiteRepository.countBySpeedTestIdentifier(FAST_IDENTIFIER) == 7);
    }

    @Test
    public void findBySpeedTestIdentifierHot() {
        List<SpeedTestWebSiteDAO> allHotSpeedTest = speedTestWebSiteRepository.findBySpeedTestIdentifier(HOT_IDENTIFIER);
        assertTrue(allHotSpeedTest.size() == 10);
    }

    @Test
    public void findMinSpeedTestWebSite() {
        String minIdentifier = speedTestWebSiteRepository.findMinIdentifier();
        assertEquals(BEZEQ_IDENTIFIER, minIdentifier);
    }
}