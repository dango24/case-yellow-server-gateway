package com.caseyellow.server.central.domain.test.model;

import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;

/**
 * Created by Dan on 12/10/2016.
 */
public class ComparisonInfo {

    private SpeedTestWebSite speedTestWebSite;
    private FileDownloadInfo fileDownloadInfo;

    public ComparisonInfo() {
    }

    public ComparisonInfo(SpeedTestWebSite speedTestWebSite, FileDownloadInfo fileDownloadInfo) {
        this.speedTestWebSite = speedTestWebSite;
        this.fileDownloadInfo = fileDownloadInfo;
    }

    public SpeedTestWebSite getSpeedTestWebSite() {
        return speedTestWebSite;
    }

    public FileDownloadInfo getFileDownloadInfo() {
        return fileDownloadInfo;
    }

    @Override
    public String toString() {
        return "ComparisonInfo{" +
                "speedTestWebSite=" + speedTestWebSite +
                ", fileDownloadInfo=" + fileDownloadInfo +
                '}';
    }
}
