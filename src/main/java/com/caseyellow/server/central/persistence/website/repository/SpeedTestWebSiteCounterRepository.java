package com.caseyellow.server.central.persistence.website.repository;

import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public interface SpeedTestWebSiteCounterRepository extends JpaRepository<SpeedTestWebSiteCounter, Long> {

    String DEFAULT_IDENTIFIER = "hot";
    String UPDATE_COUNTER_QUERY = "UPDATE SpeedTestWebSiteCounter s set s.count = s.count+1 where s.id = :id";
    String DECREASE_COUNTER_QUERY = "UPDATE SpeedTestWebSiteCounter s set s.count = s.count-1 where s.id = :id";
    String ACTIVATION_QUERY = "UPDATE SpeedTestWebSiteCounter s set s.active = :active where s.id = :id";

    SpeedTestWebSiteCounter findByIdentifier(String identifier);

    @Modifying
    @Query(UPDATE_COUNTER_QUERY)
    @Transactional(propagation = Propagation.REQUIRED)
    void updateCounter(@Param("id") long id);

    @Modifying
    @Query(DECREASE_COUNTER_QUERY)
    @Transactional(propagation = Propagation.REQUIRED)
    void decreaseCounter(@Param("id") long id);

    @Modifying
    @Query(ACTIVATION_QUERY)
    @Transactional(propagation = Propagation.REQUIRED)
    void updateActivation(@Param("id")long id, @Param("active")boolean active);

    default void addSpeedTestWebSite(String identifier) {
        SpeedTestWebSiteCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (isNull(speedTestWebSiteCounter)) {
            save(new SpeedTestWebSiteCounter(identifier));
        } else {
            updateCounter(speedTestWebSiteCounter.getId());
        }
    }


    default void reduceSpeedTestWebSite(String identifier) {
        SpeedTestWebSiteCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (isNull(speedTestWebSiteCounter)) {
            save(new SpeedTestWebSiteCounter(identifier));
        } else {
            decreaseCounter(speedTestWebSiteCounter.getId());
        }
    }

    default List<String> getIdentifiers() {
        return findAll().stream()
                        .map(SpeedTestWebSiteCounter::getIdentifier)
                        .collect(Collectors.toList());
    }

    default String findMinIdentifier() {
        Map<String, Integer> identifiers =
                findAll().stream()
                         .filter(SpeedTestWebSiteCounter::isActive)
                         .collect(toMap(SpeedTestWebSiteCounter::getIdentifier, SpeedTestWebSiteCounter::getCount));

        if (identifiers.isEmpty()) {
            return DEFAULT_IDENTIFIER;
        }

        return identifiers.entrySet()
                          .stream()
                          .min(Map.Entry.comparingByValue())
                          .map(Map.Entry::getKey)
                          .get();
    }

    default void activeSpeedTestWebSite(String identifier) {
        SpeedTestWebSiteCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (nonNull(speedTestWebSiteCounter)) {
            updateActivation(speedTestWebSiteCounter.getId(), true);
        }
    }

    default void deActiveSpeedTestWebSite(String identifier) {
        SpeedTestWebSiteCounter speedTestWebSiteCounter = findByIdentifier(identifier);

        if (nonNull(speedTestWebSiteCounter)) {
            updateActivation(speedTestWebSiteCounter.getId(), false);
        }
    }
}
