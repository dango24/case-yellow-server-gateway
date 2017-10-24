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
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public interface FileDownloadInfoCounterRepository extends JpaRepository<FileDownloadCounter, Long> {

    String UPDATE_COUNTER_QUERY = "UPDATE FileDownloadCounter f set f.count = f.count+1 where f.id = :id";
    String DECREASE_COUNTER_QUERY = "UPDATE FileDownloadCounter f set f.count = f.count-1 where f.id = :id";
    String ACTIVATION_QUERY = "UPDATE FileDownloadCounter f set f.active = :active where f.id = :id";

    FileDownloadCounter findByIdentifier(String identifier);

    @Modifying
    @Transactional
    @Query(UPDATE_COUNTER_QUERY)
    void updateCounter(@Param("id")long id);


    @Modifying
    @Transactional
    @Query(DECREASE_COUNTER_QUERY)
    void decreaseCounter(@Param("id")long id);

    @Modifying
    @Transactional
    @Query(ACTIVATION_QUERY)
    void updateActivation(@Param("id")long id, @Param("active")boolean active);

    default void addFileDownloadInfo(String identifier) {
        FileDownloadCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (isNull(speedTestWebSiteCounter)) {
            save(new FileDownloadCounter(identifier));
        } else {
            updateCounter(speedTestWebSiteCounter.getId());
        }
    }


    default void reduceFileDownloadInfo(String identifier) {
        FileDownloadCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (isNull(speedTestWebSiteCounter)) {
            save(new FileDownloadCounter(identifier));
        } else {
            decreaseCounter(speedTestWebSiteCounter.getId());
        }
    }

    default void activeFileDownloadInfo(String identifier) {
        FileDownloadCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (nonNull(speedTestWebSiteCounter)) {
            updateActivation(speedTestWebSiteCounter.getId(), true);
        }
    }

    default void deActiveFileDownloadInfo(String identifier) {
        FileDownloadCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (nonNull(speedTestWebSiteCounter)) {
            updateActivation(speedTestWebSiteCounter.getId(), false);
        }
    }

    default List<String> getIdentifiers() {
        return findAll().stream()
                        .map(FileDownloadCounter::getIdentifier)
                        .collect(Collectors.toList());
    }

    default Map<String, Integer> groupingFileDownloadInfoByIdentifier() {
        return findAll().stream()
                        .filter(FileDownloadCounter::isActive)
                        .collect(toMap(FileDownloadCounter::getIdentifier, FileDownloadCounter::getCount));
    }
}
