package com.caseyellow.server.central.persistence.statistics.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.analyzer.model.UserDownloadRateInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "cy_user_statistics")
public class UserInfo {

    @DynamoDBHashKey(attributeName = "user_name")
    private String user;

    @DynamoDBRangeKey(attributeName = "date_timestamp")
    private long timestamp;

    @DynamoDBAttribute(attributeName = "identifier_details")
    private Map<String, IdentifierDetails> identifierDetails;

    @DynamoDBAttribute(attributeName = "path")
    private String path;

    @DynamoDBAttribute(attributeName = "mean_rate")
    private Map<String, UserDownloadRateInfo> meanRate;

    public UserInfo(String user, long timestamp) {
        this(user, timestamp, null, null, null);
    }

    public UserInfo(String user, long timestamp, Map<String, IdentifierDetails> identifierDetails) {
        this(user, timestamp, identifierDetails, null, null);
    }

    public UserInfo(String user, long timestamp, String path) {
        this(user, timestamp, null, path, null);
    }

}