package com.caseyellow.server.central.persistence.website.repository;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteCounter;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class SpeedTestWebSiteCounterRepositoryTest {

    private static final String HOT_IDENTIFIER = "hot";
    private static final String BEZEQ_IDENTIFIER = "bezeq";
    private static final String FAST_IDENTIFIER = "fast";
    private static final String OOKLA_IDENTIFIER = "ookla";


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

    @Test
    public void deActiveHOTFileDownloadInfo() throws Exception {
        addSpeedTest();
        assertEquals(4, getActivateIdentifiers());
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(HOT_IDENTIFIER);
        assertEquals(3, getActivateIdentifiers());

        assertFalse(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER).isActive());
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(BEZEQ_IDENTIFIER).isActive());
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(FAST_IDENTIFIER).isActive());
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(OOKLA_IDENTIFIER).isActive());
    }

    @Test
    public void deActiveOneFileDownloadInfo() throws Exception {
        addSpeedTest();
        assertEquals(4, getActivateIdentifiers());
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(HOT_IDENTIFIER);
        assertEquals(3, getActivateIdentifiers());
    }

    @Test
    public void deActiveMultiTimesSameFileDownloadInfo() throws Exception {
        addSpeedTest();
        assertEquals(4, getActivateIdentifiers());
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(HOT_IDENTIFIER);

        assertEquals(3, getActivateIdentifiers());

        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(HOT_IDENTIFIER);
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(HOT_IDENTIFIER);

        assertEquals(3, getActivateIdentifiers());
    }

    @Test
    public void deActiveMultiFileDownloadInfo() throws Exception {
        addSpeedTest();
        assertEquals(4, getActivateIdentifiers());
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(HOT_IDENTIFIER);
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(BEZEQ_IDENTIFIER);

        assertEquals(2, getActivateIdentifiers());

        assertFalse(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER).isActive());
        assertFalse(speedTestWebSiteCounterRepository.findByIdentifier(BEZEQ_IDENTIFIER).isActive());
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(FAST_IDENTIFIER).isActive());
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(OOKLA_IDENTIFIER).isActive());
    }

    @Test
    public void deActiveAllFileDownloadInfo() throws Exception {
        addSpeedTest();
        assertEquals(4, getActivateIdentifiers());
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(HOT_IDENTIFIER);
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(BEZEQ_IDENTIFIER);
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(FAST_IDENTIFIER);
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(OOKLA_IDENTIFIER);

        assertEquals(0, getActivateIdentifiers());

        assertFalse(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER).isActive());
        assertFalse(speedTestWebSiteCounterRepository.findByIdentifier(BEZEQ_IDENTIFIER).isActive());
        assertFalse(speedTestWebSiteCounterRepository.findByIdentifier(FAST_IDENTIFIER).isActive());
        assertFalse(speedTestWebSiteCounterRepository.findByIdentifier(OOKLA_IDENTIFIER).isActive());
    }

    @Test
    public void reActiveOneFileDownloadInfo() throws Exception {
        addSpeedTest();
        assertEquals(4, getActivateIdentifiers());
        speedTestWebSiteCounterRepository.deActiveSpeedTestWebSite(HOT_IDENTIFIER);

        assertEquals(3, getActivateIdentifiers());
        assertFalse(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER).isActive());

        speedTestWebSiteCounterRepository.activeSpeedTestWebSite(HOT_IDENTIFIER);
        assertEquals(4, getActivateIdentifiers());
        assertTrue(speedTestWebSiteCounterRepository.findByIdentifier(HOT_IDENTIFIER).isActive());
    }

    private long getActivateIdentifiers() {
        return speedTestWebSiteCounterRepository.findAll()
                                                .stream()
                                                .filter(SpeedTestWebSiteCounter::isActive)
                                                .count();
    }

    private void addSpeedTest() {
        IntStream.range(0, 10).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(HOT_IDENTIFIER));
        IntStream.range(0, 20).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(BEZEQ_IDENTIFIER));
        IntStream.range(0, 30).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(FAST_IDENTIFIER));
        IntStream.range(0, 40).forEach(i -> speedTestWebSiteCounterRepository.addSpeedTestWebSite(OOKLA_IDENTIFIER));

    }
}