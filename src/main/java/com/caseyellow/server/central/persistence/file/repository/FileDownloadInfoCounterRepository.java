package com.caseyellow.server.central.persistence.file.repository;

import com.caseyellow.server.central.persistence.file.dao.FileDownloadCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

public interface FileDownloadInfoCounterRepository extends JpaRepository<FileDownloadCounter, Long> {

    String UPDATE_COUNTER_QUERY = "UPDATE FileDownloadCounter s set s.count = s.count+1 where s.id = :id";

    FileDownloadCounter findByIdentifier(String identifier);

    @Modifying
    @Transactional
    @Query(UPDATE_COUNTER_QUERY)
    void updateCounter(@Param("id") long id);

    default void addFileDownloadInfo(String identifier) {
        FileDownloadCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (isNull(speedTestWebSiteCounter)) {
            save(new FileDownloadCounter(identifier));
        } else {
            updateCounter(speedTestWebSiteCounter.getId());
        }
    }

    default List<String> getIdentifiers() {
        return findAll().stream()
                        .map(FileDownloadCounter::getIdentifier)
                        .collect(Collectors.toList());
    }


    default Map<String, Integer> groupingFileDownloadInfoByName() {
        return findAll().stream()
                        .filter(FileDownloadCounter::isActive)
                        .collect(toMap(FileDownloadCounter::getIdentifier, FileDownloadCounter::getCount));
    }
}
