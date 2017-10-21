package com.caseyellow.server.central.persistence.test.dao;

import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_download_info_id", unique = true)
    private FileDownloadInfoDAO fileDownloadInfoDAO;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "speed_test_webSite_download_info_id", unique = true)
    private SpeedTestWebSiteDAO speedTestWebSiteDAO;

    public ComparisonInfoDAO() {}

    public ComparisonInfoDAO(SpeedTestWebSiteDAO speedTestWebSiteDownloadInfoDAO, FileDownloadInfoDAO fileDownloadInfoDAO) {
        this.speedTestWebSiteDAO = speedTestWebSiteDownloadInfoDAO;
        this.fileDownloadInfoDAO = fileDownloadInfoDAO;
    }

    public SpeedTestWebSiteDAO getSpeedTestWebSiteDAO() {
        return speedTestWebSiteDAO;
    }

    public FileDownloadInfoDAO getFileDownloadInfoDAO() {
        return fileDownloadInfoDAO;
    }

    public void setFileDownloadInfoDAO(FileDownloadInfoDAO fileDownloadInfoDAO) {
        this.fileDownloadInfoDAO = fileDownloadInfoDAO;
    }

    public void setSpeedTestWebSiteDAO(SpeedTestWebSiteDAO speedTestWebSiteDAO) {
        this.speedTestWebSiteDAO = speedTestWebSiteDAO;
    }
}
