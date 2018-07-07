package com.caseyellow.server.central.domain.webSite.services;

import com.caseyellow.server.central.common.Utils;
import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.mail.EmailService;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestMetaData;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestNonFlashMetaData;
import com.caseyellow.server.central.domain.webSite.model.SuspiciousTestRatioDetails;
import com.caseyellow.server.central.domain.webSite.model.WordIdentifier;
import com.caseyellow.server.central.services.storage.FileStorageService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.caseyellow.server.central.common.Utils.*;
import static com.caseyellow.server.central.domain.mail.EmailServiceImpl.LIGHT_DATE_FORMAT;
import static java.util.Objects.nonNull;

/**
 * Created by dango on 9/19/17.
 */
@Slf4j
@Service
@ConfigurationProperties(prefix = "speedTest")
public class SpeedTestWebSiteServiceImpl implements SpeedTestWebSiteService {

    private static final String SUSPICIOUS_TEST_RATIO_BASE_COMMAND = "/usr/bin/python /home/ec2-user/case-yellow/susp_images_service/susp_images.py %s %s";
//    private static final String SUSPICIOUS_TEST_RATIO_BASE_COMMAND = "python C:\\Users\\Dan\\Desktop\\oren_efes\\susp_images.py %s %s";

    @Value("${run.extra.identifiers}")
    private boolean runExtraIdentifiers;

    @Value("${successful_tests_dir}")
    private String successfulTestsDir;

    @Getter @Setter
    private List<String> extraIdentifiers;

    private File speedTestImagesDirPath;
    private UrlConfig urlMapper;
    private FileStorageService fileStorageService;
    private EmailService emailService;
    private SpeedTestWebSiteFactory speedTestWebSiteFactory;

    @Autowired
    public SpeedTestWebSiteServiceImpl(UrlConfig urlMapper,
                                       FileStorageService fileStorageService,
                                       EmailService emailService,
                                       SpeedTestWebSiteFactory speedTestWebSiteFactory) {

        this.speedTestImagesDirPath = Utils.getTmpDir();;
        this.urlMapper = urlMapper;
        this.emailService = emailService;
        this.fileStorageService = fileStorageService;
        this.speedTestWebSiteFactory = speedTestWebSiteFactory;
    }

    @Override
    public SpeedTestMetaData getNextSpeedTestWebSite() {
        List<String> speedTestIdentifiers = new ArrayList<>(urlMapper.getSpeedTestIdentifiers());

        if (runExtraIdentifiers && nonNull(extraIdentifiers)) {
            speedTestIdentifiers.addAll(extraIdentifiers);
        }

        int random = ThreadLocalRandom.current().nextInt(speedTestIdentifiers.size());

        return speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(speedTestIdentifiers.get(random));
    }

    @Override
    public Set<WordIdentifier> getTextIdentifiers(String identifier, boolean startTest) {
        SpeedTestMetaData speedTestMetaData = speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(identifier);

        if (startTest) {
            return speedTestMetaData.getSpeedTestFlashMetaData().getButtonIds();
        } else {
            return speedTestMetaData.getSpeedTestFlashMetaData().getFinishIdentifiers();
        }
    }

    @Override
    public SpeedTestNonFlashMetaData getSpeedTestNonFlashMetaData(String identifier) {
        SpeedTestMetaData speedTestMetaData = speedTestWebSiteFactory.getSpeedTestWebSiteFromIdentifier(identifier);
        return speedTestMetaData.getSpeedTestNonFlashMetaData();
    }

    @Override
    public void investigateSuspiciousTestRatio(String outliarRatio, String hours) {
        File tmpZipFile = null;

        try {
            tmpZipFile = new File(System.getProperty("java.io.tmpdir"), "image_sanity.zip");
            String command = String.format(SUSPICIOUS_TEST_RATIO_BASE_COMMAND, outliarRatio, hours);
            String output = executeCommand(command);

            Arrays.asList(output.split(getDelimiter(output)))
                  .stream()
                  .map(rawData -> new SuspiciousTestRatioDetails(rawData, successfulTestsDir))
                  .forEach(suspiciousTestRatioDetails -> addImageToTempDir(suspiciousTestRatioDetails, speedTestImagesDirPath));

            Utils.archiveDir(speedTestImagesDirPath.getAbsolutePath(), tmpZipFile.getAbsolutePath());
            String imageSanityURL = fileStorageService.uploadFile(String.format("image-sanity/image_sanity %s.zip", LIGHT_DATE_FORMAT.format(new Date())), tmpZipFile);

            emailService.sendImageSanity(imageSanityURL);

        } catch (Exception e) {
            log.error(String.format("Failed to investigate suspicious test ratio, %s", e.getMessage()), e);
        } finally {
            deleteFile(tmpZipFile);
            cleanDirectory(speedTestImagesDirPath);
        }
    }

    private void addImageToTempDir(SuspiciousTestRatioDetails suspiciousTestRatioDetails, File dirPath) {
        File tmpFile = null;

        try {
            log.info(String.format("Fetching %s  from s3", suspiciousTestRatioDetails.getS3Path()));
            tmpFile = fileStorageService.getFile(suspiciousTestRatioDetails.getS3Path());
            Files.copy(tmpFile.toPath(), new File(dirPath, suspiciousTestRatioDetails.getName()).toPath());

        } catch (Exception e) {
            log.error(String.format("Failed to fetch %s from s3", suspiciousTestRatioDetails.getS3Path()));
            deleteFile(tmpFile);
        }
    }

    private String getDelimiter(String output) {
        if (output.contains("\r\n")) {
            return "\r\n";
        } else {
            return "\n";
        }
    }

}
