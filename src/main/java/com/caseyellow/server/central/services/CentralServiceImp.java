package com.caseyellow.server.central.services;

import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.domain.test.services.TestService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dango on 8/15/17.
 */
@Service
public class CentralServiceImp implements TestService {

    @Override
    public void saveTest(Test test) {

        /*CompletableFuture.supplyAsync(() -> test)
                         .thenAccept()*/
    }

    @Override
    public String getNextSpeedTestWebSite() {
        return "dango";
    }

    @Override
    public List<String> getNextUrls(int numOfComparisonPerTest) {
        return Arrays.asList("dan", "golan");
    }
}
