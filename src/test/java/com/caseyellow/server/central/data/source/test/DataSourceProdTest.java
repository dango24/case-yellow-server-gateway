package com.caseyellow.server.central.data.source.test;

import com.caseyellow.server.central.bootstrap.DevBootApp;
import com.caseyellow.server.central.data.source.FakeDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevBootApp.class)
@ActiveProfiles("prod")
public class DataSourceProdTest {

    @Autowired
    FakeDataSource fakeDataSource;

    @Test
    public void testDataSource() throws Exception{
        assertEquals("We ware in production", fakeDataSource.getConnection());
    }
}
