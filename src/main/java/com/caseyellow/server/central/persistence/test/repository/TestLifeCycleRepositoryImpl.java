package com.caseyellow.server.central.persistence.test.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.caseyellow.server.central.persistence.test.model.UserLifeCycle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Profile("prod")
public class TestLifeCycleRepositoryImpl implements TestLifeCycleRepository {

    private DynamoDBMapper dynamoMapper;

    @Autowired
    public TestLifeCycleRepositoryImpl(AmazonDynamoDB dynamoDBClient) {
        this.dynamoMapper = new DynamoDBMapper(dynamoDBClient);
    }

    @Override
    public void updateTestLifeCycle(String user) {
        if (StringUtils.isEmpty(user)) {
            return;
        }

        UserLifeCycle userLifeCycle = getUserLifeCycle(user);

        userLifeCycle.setLifeCycle( userLifeCycle.getLifeCycle() +1 );
        save(userLifeCycle);
    }

    @Override
    public int getUserTestLifeCycle(String user) {
        UserLifeCycle userLifeCycle = getUserLifeCycle(user);
        return userLifeCycle.getLifeCycle();
    }


    private void save(UserLifeCycle userLifeCycle) {
        try {
            dynamoMapper.save(userLifeCycle);

        } catch (Exception e) {
            log.error(String.format("Failed to save user statistics: %s", e.getMessage()), e);
        }
    }

    private UserLifeCycle getUserLifeCycle(String key) {
        UserLifeCycle userLifeCycle = new UserLifeCycle(key);

        try {
            DynamoDBQueryExpression<UserLifeCycle> queryExpression =
                    new DynamoDBQueryExpression<UserLifeCycle>().withHashKeyValues(new UserLifeCycle(key));

            List<UserLifeCycle> items = dynamoMapper.query(UserLifeCycle.class, queryExpression);

            if (items.isEmpty()) {
                return userLifeCycle;
            }

            return items.get(0);

        } catch (Exception e) {
            log.error(String.format("Failed to get last user identifiers details, cause %s", e.getMessage()), e);
            return null;
        }
    }
}
