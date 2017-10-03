package com.caseyellow.server.central.persistence.repository;

import com.caseyellow.server.central.persistence.model.SpeedTestWebSiteDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by dango on 9/19/17.
 */
public interface SpeedTestWebSiteRepository extends JpaRepository<SpeedTestWebSiteDAO, Long> {

    Long countBySpeedTestIdentifier(String speedTestIdentifier);
    List<SpeedTestWebSiteDAO> findBySpeedTestIdentifier(String speedTestIdentifier);

    default String findMinIdentifier() {

        Map<String, Long> identifiers = findAll().stream()
                                                 .map(SpeedTestWebSiteDAO::getSpeedTestIdentifier)
                                                 .collect(groupingBy(Function.identity(), counting()));

        return identifiers.entrySet()
                          .stream()
                          .min(Map.Entry.comparingByValue())
                          .map(Map.Entry::getKey)
                          .get();
    }
}
