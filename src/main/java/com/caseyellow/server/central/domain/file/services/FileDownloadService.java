package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.domain.file.model.FileDownloadProperties;

import java.util.List;

public interface FileDownloadService {
    List<FileDownloadProperties> getNextFileDownloadMetaData(int numOfComparisonPerTest);
}
