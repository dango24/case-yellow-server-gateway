package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.App;
import com.caseyellow.server.central.persistence.test.model.FileDownloadInfoDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by dango on 9/19/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= App.class)
public class FileDownloadInfoRepositoryTest {

    private static final String FIREFOX = "ftp.mozilla.org/pub/firefox/releases/37.0b1/win32/en-US/Firefox%20Setup%2037.0b1.exe";
    private static final String GO = "storage.googleapis.com/golang/go1.7.1.windows-amd64.msi";
    private static final String JAVA_SDK = "sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip";

    @Autowired
    private FileDownloadInfoRepository fileDownloadInfoRepository;

    @Before
    public void setUp() throws Exception {
        IntStream.range(0, 100).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(FIREFOX)));
        IntStream.range(0, 53).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(GO)));
        IntStream.range(0, 61).forEach(i -> fileDownloadInfoRepository.save(new FileDownloadInfoDAO(JAVA_SDK)));
    }

    @After
    public void tearDown() throws Exception {
        fileDownloadInfoRepository.deleteAll();
    }

    @Test
    public void allFileDownloadInfoDAOAdded() throws Exception {
        assertTrue(fileDownloadInfoRepository.findAll().size() == 214);
    }


    @Test
    public void findByFileURL() throws Exception {
        assertTrue(fileDownloadInfoRepository.findByFileURL(FIREFOX).size() == 100);
    }

    @Test
    public void groupingFileDownloadInfoByUrl() throws Exception {
        Map<String, Long> fileDownloadInfoMap= fileDownloadInfoRepository.groupingFileDownloadInfoByUrl();

        assertTrue(fileDownloadInfoMap.size() == 3);

        assertNotNull(fileDownloadInfoMap.get(FIREFOX));
        assertNotNull(fileDownloadInfoMap.get(GO));
        assertNotNull(fileDownloadInfoMap.get(JAVA_SDK));

        assertTrue(fileDownloadInfoMap.get(FIREFOX) == 100);
        assertTrue(fileDownloadInfoMap.get(GO) == 53);
        assertTrue(fileDownloadInfoMap.get(JAVA_SDK) == 61);
    }

}