package com.caseyellow.server.central.domain.test.model;

import com.caseyellow.server.central.domain.file.model.FileDownloadInfo;
import com.caseyellow.server.central.domain.webSite.model.SpeedTestWebSite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Dan on 12/10/2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonInfo {

    private SpeedTestWebSite speedTestWebSite;
    private FileDownloadInfo fileDownloadInfo;
}
