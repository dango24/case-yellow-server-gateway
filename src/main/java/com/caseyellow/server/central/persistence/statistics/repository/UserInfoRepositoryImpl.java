package com.caseyellow.server.central.persistence.statistics.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.analyzer.model.UserDownloadRateInfo;
import com.caseyellow.server.central.persistence.statistics.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Service
@Profile("prod")
public class UserInfoRepositoryImpl implements UserInfoRepository {

    @Value("${dynamo.user.statistics.table.name}")
    private String userStatisticsTableName;

    private Table userStatisticsTable;
    private DynamoDBMapper dynamoMapper;
    private AmazonDynamoDB dynamoDB;

    @Autowired
    public UserInfoRepositoryImpl(AmazonDynamoDB dynamoDBClient) {
        this.dynamoDB = dynamoDBClient;
    }

    @PostConstruct
    private void init() {
        this.dynamoMapper = new DynamoDBMapper(dynamoDB);
        this.userStatisticsTable = new DynamoDB(dynamoDB).getTable(userStatisticsTableName);
    }

    @Override
    public void saveIdentifiersDetails(String user, Map<String, IdentifierDetails> identifierDetails) {
        if (StringUtils.isEmpty(user) || isNull(identifierDetails) || identifierDetails.isEmpty()) {
            return;
        }

        UserInfo userInfo =
                new UserInfo(user, System.currentTimeMillis(), identifierDetails);

        save(userInfo);

        log.info(String.format("Successfully save new user statistics for user: %s, with timestamp: %s", userInfo.getUser(), userInfo.getTimestamp()));


    }

    @Override
    public void saveUserMeanRate(String user, Map<String, UserDownloadRateInfo> meanRate) {

        UserInfo userInfo =
                new UserInfo(user + "-mean-rate", System.currentTimeMillis());

        userInfo.setMeanRate(meanRate);

        save(userInfo);

        log.info(String.format("Successfully save new user path for user: %s, with timestamp: %s, with mean rate: %s", userInfo.getUser(), userInfo.getTimestamp(), meanRate));
    }

    @Override
    public void saveUserPath(String user, String path) {

        UserInfo userInfo =
                new UserInfo(user, System.currentTimeMillis(), path);

        save(userInfo);

        log.info(String.format("Successfully save new user path for user: %s, with timestamp: %s, with path: %s", userInfo.getUser(), userInfo.getTimestamp(), path));
    }

    private void save(UserInfo userInfo) {
        try {
            dynamoMapper.save(userInfo);

        } catch (Exception e) {
            log.error(String.format("Failed to save user statistics: %s", e.getMessage()), e);
        }
    }

    @Override
    public Map<String, IdentifierDetails> getLastUserStatistics(String user) {
        if (StringUtils.isEmpty(user)) {
            return Collections.emptyMap();
        }

        Map<String, IdentifierDetails> userDetails =
                (Map<String, IdentifierDetails>) getLastSortAttributeFromItem(user, "identifier_details");

        if (isNull(userDetails)) {
            return Collections.emptyMap();
        }

        return userDetails;
    }

    @Override
    public Map<String, UserDownloadRateInfo> getMeanRate(String user) {
        if (StringUtils.isEmpty(user)) {
            return Collections.emptyMap();
        }

        Map<String, UserDownloadRateInfo> userDetails =
                (Map<String, UserDownloadRateInfo>) getLastSortAttributeFromItem(user + "-mean-rate", "mean_rate");

        if (isNull(userDetails)) {
            return Collections.emptyMap();
        }

        return userDetails;
    }

    @Override
    public String getLastUserPath(String user) {
        String lastUserPath = String.valueOf(getLastSortAttributeFromItem(user, "path"));

        if (StringUtils.isEmpty(lastUserPath)) {
            return null;
        }

        return lastUserPath;
    }

    private Object getLastSortAttributeFromItem(String key, String attribute) {
        Item mostRecentlyItem;
        List<Item> items;

        try {
            QuerySpec querySpec =
                    new QuerySpec().withKeyConditionExpression("user_name = :_user")
                                   .withValueMap(new ValueMap().withString(":_user", key))
                                   .withMaxResultSize(1) // Return only the most updated user identifier details
                                   .withScanIndexForward(false);

            items = IteratorUtils.toList(userStatisticsTable.query(querySpec).iterator());

            if (items.size() <= 0) {
                return Collections.emptyMap();
            }

            mostRecentlyItem = items.get(0);

            return mostRecentlyItem.get(attribute);

        } catch (Exception e) {
            log.error(String.format("Failed to get last user identifiers details, cause %s", e.getMessage()), e);
            return null;
        }
    }

}
