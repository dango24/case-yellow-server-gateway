package com.caseyellow.server.central.domain.file.services;

import com.caseyellow.server.central.domain.file.model.FileDownloadMetaData;

import java.util.List;

public interface FileDownloadService {
    List<FileDownloadMetaData> getNextFileDownloadMetaData(int numOfComparisonPerTest);
}
