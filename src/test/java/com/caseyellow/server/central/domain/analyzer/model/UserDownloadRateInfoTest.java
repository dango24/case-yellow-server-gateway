package com.caseyellow.server.central.domain.analyzer.model;

import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

@Ignore
public class UserDownloadRateInfoTest {


    @Test
    public void generateTestCountStr() throws Exception {
        UserDownloadRateInfo userDownloadRateInfo = new UserDownloadRateInfo();
        userDownloadRateInfo.setTestCount(324);

        assertEquals("972", invokeGenerateTestCountStr(userDownloadRateInfo));
    }

    @Test
    public void generateTestCountStr2() throws Exception {
        UserDownloadRateInfo userDownloadRateInfo = new UserDownloadRateInfo();
        userDownloadRateInfo.setTestCount(1);

        assertEquals("3", invokeGenerateTestCountStr(userDownloadRateInfo));
    }

    @Test
    public void generateTestCountStr3() throws Exception {
        UserDownloadRateInfo userDownloadRateInfo = new UserDownloadRateInfo();
        userDownloadRateInfo.setTestCount(0);

        assertEquals("0", invokeGenerateTestCountStr(userDownloadRateInfo));
    }

    @Test
    public void generateTestCountStr4() throws Exception {
        UserDownloadRateInfo userDownloadRateInfo = new UserDownloadRateInfo();
        userDownloadRateInfo.setTestCount(5000);

        assertEquals("15,000", invokeGenerateTestCountStr(userDownloadRateInfo));
    }

    private String invokeGenerateTestCountStr(UserDownloadRateInfo userDownloadRateInfo) throws Exception {
        Method generateTestCountStrMethod = UserDownloadRateInfo.class.getDeclaredMethod("generateTestCountStr");
        generateTestCountStrMethod.setAccessible(true);

        return String.valueOf(generateTestCountStrMethod.invoke(userDownloadRateInfo));
    }

}