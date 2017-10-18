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
    private static S3FileUploadService s3FileUploadService;

    @BeforeClass
    public static void setUp() throws Exception {
        s3FileUploadService = new S3FileUploadService();
        createFileUniquePathMethod = S3FileUploadService.class.getDeclaredMethod("createFileUniquePath", String.class, String.class);
        createFileUniquePathMethod.setAccessible(true);
    }

    @Test
    public void createFileUniquePathTest() throws Exception {
        String ip = "192.14.1.1";
        String fileName = "dango.png";
        String path = (String)createFileUniquePathMethod.invoke(s3FileUploadService, ip, fileName);

        assertNotNull(path);
        assertTrue(path.endsWith(fileName));
        assertTrue(path.startsWith("1921411"));
    }

}