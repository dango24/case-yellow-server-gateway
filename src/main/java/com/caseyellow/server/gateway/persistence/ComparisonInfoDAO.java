package com.caseyellow.server.gateway.persistence;

import javax.persistence.*;

/**
 * Created by Dan on 12/10/2016.
 */
@Entity
@Table(name = "comparison_info")
public class ComparisonInfoDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "file_download_info_id", unique = true)
    private FileDownloadInfoDAO fileDownloadInfoDAO;

    @OneToOne
    @JoinColumn(name = "speed_test_webSite_download_info_id", unique = true)
    private SpeedTestWebSiteDownloadInfoDAO speedTestWebSiteDownloadInfoDAO;

    public ComparisonInfoDAO() {}

    public ComparisonInfoDAO(SpeedTestWebSiteDownloadInfoDAO speedTestWebSiteDownloadInfoDAO, FileDownloadInfoDAO fileDownloadInfoDAO) {
        this.speedTestWebSiteDownloadInfoDAO = speedTestWebSiteDownloadInfoDAO;
        this.fileDownloadInfoDAO = fileDownloadInfoDAO;
    }

    public SpeedTestWebSiteDownloadInfoDAO getSpeedTestWebSiteDownloadInfoDAO() {
        return speedTestWebSiteDownloadInfoDAO;
    }

    public FileDownloadInfoDAO getFileDownloadInfoDAO() {
        return fileDownloadInfoDAO;
    }

    public void setFileDownloadInfoDAO(FileDownloadInfoDAO fileDownloadInfoDAO) {
        this.fileDownloadInfoDAO = fileDownloadInfoDAO;
    }

    public void setSpeedTestWebSiteDownloadInfoDAO(SpeedTestWebSiteDownloadInfoDAO speedTestWebSiteDownloadInfoDAO) {
        this.speedTestWebSiteDownloadInfoDAO = speedTestWebSiteDownloadInfoDAO;
    }
}
