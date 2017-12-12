package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.domain.test.model.PreSignedUrl;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.model.TestWrapper;

import java.util.List;

/**
 * Created by dango on 8/15/17.
 */
public interface TestService {
    void saveTest(TestWrapper test);
    List<Test> getAllTests();
    PreSignedUrl generatePreSignedUrl(String userIP, String fileName);
}
