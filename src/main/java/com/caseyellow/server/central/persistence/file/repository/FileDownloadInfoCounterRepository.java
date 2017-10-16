package com.caseyellow.server.central.persistence.file.repository;

import com.caseyellow.server.central.persistence.file.dao.FileDownloadCounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public interface FileDownloadInfoCounterRepository extends JpaRepository<FileDownloadCounter, Long> {

    default Map<String, Integer> groupingFileDownloadInfoByName() {
        return findAll().stream()
                        .filter(FileDownloadCounter::isActive)
                        .collect(toMap(FileDownloadCounter::getIdentifier, FileDownloadCounter::getCount));
    }
}
