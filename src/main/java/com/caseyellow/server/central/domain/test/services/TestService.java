package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.domain.test.model.FailedTestDetails;
import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.UserDetails;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;

import java.util.List;
import java.util.Map;

/**
 * Created by dango on 8/15/17.
 */
public interface TestService {
    void failedTest(FailedTestDetails failedTestDetails);
    void saveTest(Test test);
    boolean isUserExist(String userName);
    List<Test> getAllTests();
    List<TestDAO> getAllDAOTests();
    Map<String, List<String>> getConnectionDetails();
    PreSignedUrl generatePreSignedUrl(String fileKey);
    void saveUserDetails(UserDetails userDetails);
}
