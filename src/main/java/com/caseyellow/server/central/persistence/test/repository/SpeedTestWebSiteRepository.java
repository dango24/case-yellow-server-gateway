package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.persistence.test.model.SpeedTestWebSiteDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        return identifiers.entrySet().stream()
                                     .min(Comparator.comparing(entry -> entry.getValue()))
                                     .map(entry -> entry.getKey())
                                     .get();
    }
}
