package caseyellow.server.gateway.services;

import caseyellow.server.gateway.domain.Test;
import caseyellow.server.gateway.domain.interfaces.CentralService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dango on 8/15/17.
 */
@Service
public class CentralServiceImp implements CentralService {

    @Override
    public void saveTest(Test test) {

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
