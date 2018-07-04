package com.caseyellow.server.central.common;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.*;

@ActiveProfiles("dev")
public class UtilsTest {


    @Test
    public void calculateDownloadRateFromKBpsToMbps() throws Exception {
        double Mbps = 40.0;
        double KBps = Utils.calculateDownloadRateFromMbpsToKBps(Mbps);

        assertEquals(Mbps, Utils.calculateDownloadRateFromKBpsToMbps(KBps), 0.01);
    }

    @Test
    public void calculateDownloadRateFromKBpsToMbps2() throws Exception {
        double Mbps = 37.04;
        double KBps = Utils.calculateDownloadRateFromMbpsToKBps(Mbps);

        assertEquals(Mbps, Utils.calculateDownloadRateFromKBpsToMbps(KBps), 0.01);
    }

}