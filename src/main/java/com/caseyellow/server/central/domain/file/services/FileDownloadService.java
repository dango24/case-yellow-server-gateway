package com.caseyellow.server.central.domain.file.services;

import java.util.List;

public interface FileDownloadService {
    List<String> getNextUrls(int numOfComparisonPerTest);
}
