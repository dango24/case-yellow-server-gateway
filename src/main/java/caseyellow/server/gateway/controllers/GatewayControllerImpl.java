package caseyellow.server.gateway.controllers;

import caseyellow.server.gateway.exceptions.TestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dango on 6/25/17.
 */
@RestController
@RequestMapping("/gateway")
public class GatewayControllerImpl implements GatewayController {

    @Override
    public String getNextSpeedTestWebSite() {
        return "atnt";
    }

    @Override
    public List<String> getNextUrls(@RequestParam("comparison-count") int numOfComparisonPerTest) {
        System.out.println(numOfComparisonPerTest);
        return Arrays.asList("A", "B", "C");
    }

    @ExceptionHandler(TestException.class)
    public void handleTestException(TestException testException, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), testException.getMessage());
    }
}
