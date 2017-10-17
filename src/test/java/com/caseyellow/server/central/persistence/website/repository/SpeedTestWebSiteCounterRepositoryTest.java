package com.caseyellow.server.central.persistence.website.repository;

import com.caseyellow.server.central.CaseYellowCentral;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class SpeedTestWebSiteCounterRepositoryTest {

    private static final String HOT_IDENTIFIER = "hot";
    private static final String BEZEQ_IDENTIFIER = "bezeq";
    private static final String FAST_IDENTIFIER = "fast";


    private SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository;

    @Autowired
    public void setSpeedTestWebSiteCounterRepository(SpeedTestWebSiteCounterRepository speedTestWebSiteCounterRepository) {
        this.speedTestWebSiteCounterRepository = speedTestWebSiteCounterRepository;
    }


    @After
    public void tearDown() throws Exception {
        speedTestWebSiteCounterRepository.deleteAll();
    }


    @Test
    public void addOneIdentifier() throws Exception {
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());

        speedTestWebSiteCounterRepository.addSpeedTestWebSite(BEZEQ_IDENTIFIER);

        assertTrue(speedTestWebSiteCounterRepository.count() == 1);
        assertTrue(speedTestWebSiteCounterRepository.findMinIdentifier().equals(BEZEQ_IDENTIFIER));
        assertNotNull(speedTestWebSiteCounterRepository.findByIdentifier(BEZEQ_IDENTIFIER));
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(BEZEQ_IDENTIFIER).getCount() == 0);
    }

    @Test
    public void addThreeIdentifier() throws Exception {
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());

        speedTestWebSiteCounterRepository.addSpeedTestWebSite(BEZEQ_IDENTIFIER);
        speedTestWebSiteCounterRepository.addSpeedTestWebSite(HOT_IDENTIFIER);
        speedTestWebSiteCounterRepository.addSpeedTestWebSite(FAST_IDENTIFIER);

        assertTrue(speedTestWebSiteCounterRepository.count() == 3);

        assertNotNull(speedTestWebSiteCounterRepository.findByIdentifier(BEZEQ_IDENTIFIER));
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(BEZEQ_IDENTIFIER).getCount() == 0);

        assertNotNull(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER));
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER).getCount() == 0);

        assertNotNull(speedTestWebSiteCounterRepository.findByIdentifier(FAST_IDENTIFIER));
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(FAST_IDENTIFIER).getCount() == 0);
    }

    @Test
    public void addWithSameIdentifier() throws Exception {
        assertTrue(speedTestWebSiteCounterRepository.findAll().isEmpty());

        speedTestWebSiteCounterRepository.addSpeedTestWebSite(HOT_IDENTIFIER);
        speedTestWebSiteCounterRepository.addSpeedTestWebSite(HOT_IDENTIFIER);
        speedTestWebSiteCounterRepository.addSpeedTestWebSite(BEZEQ_IDENTIFIER);

        assertTrue(speedTestWebSiteCounterRepository.count() == 2);
        assertTrue(speedTestWebSiteCounterRepository.findMinIdentifier().equals(BEZEQ_IDENTIFIER));
        assertNotNull(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER));
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER).getCount() == 1);
    }

}