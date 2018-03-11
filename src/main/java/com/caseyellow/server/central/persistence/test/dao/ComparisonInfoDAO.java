package com.caseyellow.server.central.persistence.test.dao;

import com.caseyellow.server.central.persistence.file.dao.FileDownloadInfoDAO;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by Dan on 12/10/2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public ComparisonInfoDAO(SpeedTestWebSiteDAO speedTestWebSiteDownloadInfoDAO, FileDownloadInfoDAO fileDownloadInfoDAO) {
        this.speedTestWebSiteDAO = speedTestWebSiteDownloadInfoDAO;
        this.fileDownloadInfoDAO = fileDownloadInfoDAO;
    }
}
