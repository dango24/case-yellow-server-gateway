package com.caseyellow.server.central.domain.webSite.model;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;

@Data
public class SuspiciousTestRatioDetails {

    private String value;
    private String s3Path;
    private String name;

    public SuspiciousTestRatioDetails(String rawData, String s3PathDir) {
        this(rawData.split(" ")[0], rawData.split(" ")[1], s3PathDir);
    }

    public SuspiciousTestRatioDetails(String value, String s3Path, String s3PathDir) {
        this.value = value;
        this.s3Path = s3PathDir + s3Path;
        this.name = createName(value, s3Path);
    }

    private String createName(String value, String s3Path) {
        String md5 = s3Path.split("-")[2];
        String extension = FilenameUtils.getExtension(s3Path);

        return value + "_____" + md5 + "." + extension;
    }
}
