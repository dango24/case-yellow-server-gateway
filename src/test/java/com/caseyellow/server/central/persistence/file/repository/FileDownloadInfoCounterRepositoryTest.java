package com.caseyellow.server.central.persistence.file.repository;

import com.caseyellow.server.central.CaseYellowCentral;
import com.caseyellow.server.central.persistence.file.dao.FileDownloadCounter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseYellowCentral.class)
@ActiveProfiles("dev")
public class FileDownloadInfoCounterRepositoryTest {

    private static final String GO = "go";
    private static final String FIREFOX = "firefox";
    private static final String JAVA_SDK = "java-sdk";
    private static final String ITUNES = "itunes";

    private FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository;

    @Autowired
    public void setFileDownloadInfoCounterRepository(FileDownloadInfoCounterRepository fileDownloadInfoCounterRepository) {
        this.fileDownloadInfoCounterRepository = fileDownloadInfoCounterRepository;
    }

    @Before
    public void setUp() throws Exception {
        IntStream.range(0, 10).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(GO));
        IntStream.range(0, 20).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(FIREFOX));
        IntStream.range(0, 30).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(JAVA_SDK));
        IntStream.range(0, 40).forEach(i -> fileDownloadInfoCounterRepository.addFileDownloadInfo(ITUNES));
    }

    @After
    public void tearDown() throws Exception {
        fileDownloadInfoCounterRepository.deleteAll();
    }

    @Test
    public void deActiveGOFileDownloadInfo() throws Exception {
        assertEquals(4, getActivateIdentifiers());
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);
        assertEquals(3, getActivateIdentifiers());

        assertFalse(fileDownloadInfoCounterRepository.findByIdentifier(GO).isActive());
        assertTrue(fileDownloadInfoCounterRepository.findByIdentifier(FIREFOX).isActive());
        assertTrue(fileDownloadInfoCounterRepository.findByIdentifier(JAVA_SDK).isActive());
        assertTrue(fileDownloadInfoCounterRepository.findByIdentifier(ITUNES).isActive());
    }

    @Test
    public void deActiveOneFileDownloadInfo() throws Exception {
        assertEquals(4, getActivateIdentifiers());
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);
        assertEquals(3, getActivateIdentifiers());
    }

    @Test
    public void deActiveMultiTimesSameFileDownloadInfo() throws Exception {
        assertEquals(4, getActivateIdentifiers());
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);

        assertEquals(3, getActivateIdentifiers());

        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);

        assertEquals(3, getActivateIdentifiers());
    }

    @Test
    public void deActiveMultiFileDownloadInfo() throws Exception {
        assertEquals(4, getActivateIdentifiers());
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(FIREFOX);

        assertEquals(2, getActivateIdentifiers());

        assertFalse(fileDownloadInfoCounterRepository.findByIdentifier(GO).isActive());
        assertFalse(fileDownloadInfoCounterRepository.findByIdentifier(FIREFOX).isActive());

        assertTrue(fileDownloadInfoCounterRepository.findByIdentifier(JAVA_SDK).isActive());
        assertTrue(fileDownloadInfoCounterRepository.findByIdentifier(ITUNES).isActive());
    }

    @Test
    public void getActiveIdentifiers() throws Exception {
        assertEquals(4, getActivateIdentifiers());
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(FIREFOX);

        assertEquals(2, getActivateIdentifiers());

        assertFalse(fileDownloadInfoCounterRepository.getActiveIdentifiers().contains(GO));
        assertFalse(fileDownloadInfoCounterRepository.getActiveIdentifiers().contains(FIREFOX));

        assertTrue(fileDownloadInfoCounterRepository.getActiveIdentifiers().contains(JAVA_SDK));
        assertTrue(fileDownloadInfoCounterRepository.getActiveIdentifiers().contains(ITUNES));
    }

    @Test
    public void deActiveAllFileDownloadInfo() throws Exception {
        assertEquals(4, getActivateIdentifiers());
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(FIREFOX);
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(JAVA_SDK);
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(ITUNES);

        assertEquals(0, getActivateIdentifiers());

        assertFalse(fileDownloadInfoCounterRepository.findByIdentifier(GO).isActive());
        assertFalse(fileDownloadInfoCounterRepository.findByIdentifier(FIREFOX).isActive());
        assertFalse(fileDownloadInfoCounterRepository.findByIdentifier(JAVA_SDK).isActive());
        assertFalse(fileDownloadInfoCounterRepository.findByIdentifier(ITUNES).isActive());
    }

    @Test
    public void reActiveOneFileDownloadInfo() throws Exception {
        assertEquals(4, getActivateIdentifiers());
        fileDownloadInfoCounterRepository.deActiveFileDownloadInfo(GO);

        assertEquals(3, getActivateIdentifiers());
        assertFalse(fileDownloadInfoCounterRepository.findByIdentifier(GO).isActive());

        fileDownloadInfoCounterRepository.activeFileDownloadInfo(GO);
        assertEquals(4, getActivateIdentifiers());
        assertTrue(fileDownloadInfoCounterRepository.findByIdentifier(GO).isActive());
    }

    private long getActivateIdentifiers() {
        return fileDownloadInfoCounterRepository.findAll()
                                                .stream()
                                                .filter(FileDownloadCounter::isActive)
                                                .count();
    }

}