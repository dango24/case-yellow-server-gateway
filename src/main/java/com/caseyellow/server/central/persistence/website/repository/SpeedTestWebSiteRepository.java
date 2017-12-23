package com.caseyellow.server.central.persistence.website.repository;

import com.caseyellow.server.central.persistence.website.dao.AnalyzedState;
import com.caseyellow.server.central.persistence.website.dao.SpeedTestWebSiteDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Created by dango on 9/19/17.
 */
public interface SpeedTestWebSiteRepository extends JpaRepository<SpeedTestWebSiteDAO, Long> {

    String UPDATE_ANALYZED_IMAGE_RESULT = "UPDATE SpeedTestWebSiteDAO s set s.downloadRateInMbps = :downloadRateInMbps  where s.id = :id";
    String UPDATE_ANALYZED_STATE = "UPDATE SpeedTestWebSiteDAO s set s.analyzedState = :analyzedState where s.id = :id";
    String SELECT_IDENTIFIER_AND_URL_QUERY = "select DISTINCT SPEED_TEST_IDENTIFIER , URL_ADDRESS  from SPEED_TEST_WEB_SITE";

    Long countBySpeedTestIdentifier(String speedTestIdentifier);
    List<SpeedTestWebSiteDAO> findBySpeedTestIdentifier(String speedTestIdentifier);
    List<SpeedTestWebSiteDAO> findByAnalyzedState(AnalyzedState analyzedState);

    @Modifying
    @Transactional
    @Query(UPDATE_ANALYZED_STATE)
    void updateAnalyzedState(@Param("id") long id, @Param("analyzedState")AnalyzedState analyzedState);

    @Query(value = SELECT_IDENTIFIER_AND_URL_QUERY, nativeQuery = true)
    List<Object[]> selectIdentifierAndURL();

    @Modifying
    @Transactional
    @Query(UPDATE_ANALYZED_IMAGE_RESULT)
    void updateAnalyzedImageResult(@Param("id") long id, @Param("downloadRateInMbps") double downloadRateInMbps);

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
