package com.caseyellow.server.central.services;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class S3FileUploadServiceTest {

    private static Method createFileUniquePathMethod;
    private static S3FileStorageService s3FileStorageService;

    @BeforeClass
    public static void setUp() throws Exception {
        s3FileStorageService = new S3FileStorageService();
        createFileUniquePathMethod = S3FileStorageService.class.getDeclaredMethod("createFileUniquePath", String.class, String.class);
        createFileUniquePathMethod.setAccessible(true);
    }

    @Test
    public void createFileUniquePathTest() throws Exception {
        String ip = "192.14.1.1";
        String fileName = "dango.png";
        String path = (String)createFileUniquePathMethod.invoke(s3FileStorageService, ip, fileName);

        assertNotNull(path);
        assertTrue(path.endsWith(fileName));
        assertTrue(path.startsWith("1921411"));
    }

}