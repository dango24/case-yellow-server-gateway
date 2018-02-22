package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.domain.test.model.FailedTest;
import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.UserDetails;

import java.util.List;
import java.util.Map;

/**
 * Created by dango on 8/15/17.
 */
public interface TestService {
    void saveTest(Test test);
    void saveUserDetails(UserDetails userDetails);
    void saveFailedTest(FailedTest failedTest);
    long userLastTest(String user);
    long userLastFailedTest(String user);
    boolean isUserExist(String userName);
    PreSignedUrl generatePreSignedUrl(String fileKey);
    List<Test> getAllTests();
    List<Test> getAllUserTests(String user);
    List<FailedTest> getAllUserFailedTests(String user);
    Map<String, List<String>> getConnectionDetails();
}
