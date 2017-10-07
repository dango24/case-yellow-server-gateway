package com.caseyellow.server.central.persistence.repository;

import com.caseyellow.server.central.persistence.model.SpeedTestWebSiteDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * Created by dango on 9/19/17.
 */
public interface SpeedTestWebSiteRepository extends JpaRepository<SpeedTestWebSiteDAO, Long> {

    String DEFAULT_IDENTIFIER = "hot";
    String SELECT_IDENTIFIERS_QUERY = "select DISTINCT SPEED_TEST_IDENTIFIER from SPEED_TEST_WEB_SITE_DOWNLOAD_INFO";
    String SELECT_IDENTIFIER_AND_URL_QUERY = "select DISTINCT SPEED_TEST_IDENTIFIER , URL_ADDRESS  from SPEED_TEST_WEB_SITE_DOWNLOAD_INFO";

    Long countBySpeedTestIdentifier(String speedTestIdentifier);
    List<SpeedTestWebSiteDAO> findBySpeedTestIdentifier(String speedTestIdentifier);

    @Query(value = SELECT_IDENTIFIER_AND_URL_QUERY, nativeQuery = true)
    List<Object[]> selectIdentifierAndURL();

    @Query(value = SELECT_IDENTIFIERS_QUERY, nativeQuery = true)
    List<String> getAllSpeedTestIdentifiers();

    default String findMinIdentifier() {

        Map<String, Long> identifiers = findAll().stream()
                                                 .map(SpeedTestWebSiteDAO::getSpeedTestIdentifier)
                                                 .collect(groupingBy(Function.identity(), counting()));
        if (identifiers.isEmpty()) {
            return DEFAULT_IDENTIFIER;
        }

        return identifiers.entrySet()
                          .stream()
                          .min(Map.Entry.comparingByValue())
                          .map(Map.Entry::getKey)
                          .get();
    }

    default Map<String, String> getIdentifierToURLMapper() {
        List<Object[]> identifierToURLList = selectIdentifierAndURL();

        if (identifierToURLList.isEmpty()) {
            return Collections.emptyMap();
        }

        return identifierToURLList.stream()
                                  .filter(record -> record.length == 2)
                                  .collect(toMap(record -> String.valueOf(record[0]),
                                                 record -> String.valueOf(record[1])));
    }

}
