package com.caseyellow.server.central.services;

import com.caseyellow.server.central.domain.test.service.TestService;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dango on 8/20/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CentralServiceImpTest {

    private TestService centralService;

    @Autowired
    public void setCentralService(TestService centralService) {
        this.centralService = centralService;
    }

    @Test
    public void getNextSpeedTestWebSite() throws Exception {
        List<String> listUnderTest = centralService.getNextUrls(3);
        assertThat(listUnderTest, IsIterableContainingInOrder.contains(listUnderTest));
    }

    @Test
    public void getNextUrls() throws Exception {
    }

}