package com.caseyellow.server.central.persistence.test.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "test_life_cycle")
public class UserLifeCycle {

    @DynamoDBHashKey(attributeName = "user_name")
    private String user;

    @DynamoDBAttribute(attributeName = "life_cycle")
    private Integer lifeCycle;

    public UserLifeCycle(String user) {
        this(user, 0);
    }
}
