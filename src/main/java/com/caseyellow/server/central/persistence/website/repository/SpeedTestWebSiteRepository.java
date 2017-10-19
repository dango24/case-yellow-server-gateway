package com.caseyellow.server.central.persistence.website.repository;

import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Created by dango on 9/19/17.
 */
public interface SpeedTestWebSiteRepository extends JpaRepository<SpeedTestWebSiteDAO, Long> {

    String SELECT_IDENTIFIERS_QUERY = "select DISTINCT SPEED_TEST_IDENTIFIER from SPEED_TEST_WEB_SITE";
    String SELECT_IDENTIFIER_AND_URL_QUERY = "select DISTINCT SPEED_TEST_IDENTIFIER , URL_ADDRESS  from SPEED_TEST_WEB_SITE";

    Long countBySpeedTestIdentifier(String speedTestIdentifier);
    List<SpeedTestWebSiteDAO> findBySpeedTestIdentifier(String speedTestIdentifier);
    List<SpeedTestWebSiteDAO> findByAnalyzedFalse();

    @Query(value = SELECT_IDENTIFIER_AND_URL_QUERY, nativeQuery = true)
    List<Object[]> selectIdentifierAndURL();

    @Query(value = SELECT_IDENTIFIERS_QUERY, nativeQuery = true)
    List<String> getAllSpeedTestIdentifiers();

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
