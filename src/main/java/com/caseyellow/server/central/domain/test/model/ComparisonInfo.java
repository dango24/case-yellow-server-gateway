package com.caseyellow.server.central.domain.test.model;

/**
 * Created by Dan on 12/10/2016.
 */
public class ComparisonInfo {

    private SpeedTestWebSite speedTestWebSiteDownloadInfo;
    private FileDownloadInfo fileDownloadInfo;

    public ComparisonInfo() {
    }

    public ComparisonInfo(SpeedTestWebSite speedTestWebSiteDownloadInfo, FileDownloadInfo fileDownloadInfo) {
        this.speedTestWebSiteDownloadInfo = speedTestWebSiteDownloadInfo;
        this.fileDownloadInfo = fileDownloadInfo;
    }

    public SpeedTestWebSite getSpeedTestWebSiteDownloadInfo() {
        return speedTestWebSiteDownloadInfo;
    }

    public FileDownloadInfo getFileDownloadInfo() {
        return fileDownloadInfo;
    }

}
