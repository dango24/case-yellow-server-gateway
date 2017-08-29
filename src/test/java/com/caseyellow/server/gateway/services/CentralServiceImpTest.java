package com.caseyellow.server.gateway.services;

import com.caseyellow.server.gateway.domain.interfaces.CentralService;
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

    private CentralService centralService;

    @Autowired
    public void setCentralService(CentralService centralService) {
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