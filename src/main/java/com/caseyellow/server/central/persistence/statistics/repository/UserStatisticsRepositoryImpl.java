package com.caseyellow.server.central.persistence.statistics.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.persistence.statistics.model.UserStatistics;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class UserStatisticsRepositoryImpl implements UserStatisticsRepository {

    @Value("${dynamo.user.statistics.table.name}")
    private String userStatisticsTableName;

    private Table userStatisticsTable;
    private DynamoDBMapper dynamoMapper;
    private AmazonDynamoDB dynamoDB;

    @Autowired
    public UserStatisticsRepositoryImpl(AmazonDynamoDB dynamoDBClient) {
        this.dynamoDB = dynamoDBClient;
    }

    @PostConstruct
    private void init() {
        this.dynamoMapper = new DynamoDBMapper(dynamoDB);
        this.userStatisticsTable = new DynamoDB(dynamoDB).getTable(userStatisticsTableName);
    }

    @Override
    public void saveUserStatistics(String user, Map<String, IdentifierDetails> identifierDetails) {
        if (StringUtils.isEmpty(user) || isNull(identifierDetails) || identifierDetails.isEmpty()) {
            return;
        }

        UserStatistics userStatistics =
                new UserStatistics(user, System.currentTimeMillis(), identifierDetails);

        dynamoMapper.save(userStatistics);
        log.info(String.format("Successfully save new user statistics for user: %s, with timestamp: %s", userStatistics.getUser(), userStatistics.getTimestamp()));
    }

    @Override
    public Map<String, IdentifierDetails> getLastUserStatistics(String user) {
        Item mostRecentlyItem;
        List<Item> items;

        try {
            QuerySpec querySpec =
                    new QuerySpec().withKeyConditionExpression("user_name = :_user")
                            .withValueMap(new ValueMap().withString(":_user", user))
                            .withMaxResultSize(1) // Return only the most updated user identifier details
                            .withScanIndexForward(false);

            items = IteratorUtils.toList(userStatisticsTable.query(querySpec).iterator());

            if (items.size() <= 0) {
                return Collections.emptyMap();
            }

            mostRecentlyItem = items.get(0);

            return (Map<String, IdentifierDetails>)mostRecentlyItem.get("identifier_details");

        } catch (Exception e) {
            log.error(String.format("Failed to get last user identifiers details, cause %s", e.getMessage()), e);
            return Collections.emptyMap();
        }
    }


}
