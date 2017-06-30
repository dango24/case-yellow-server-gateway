package caseyellow.server.gateway.controllers;

import caseyellow.server.gateway.exceptions.TestException;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        return Arrays.asList("http://mirrors.kodi.tv/releases/osx/x86_64/kodi-17.3-Krypton-x86_64.dmg",
                             "https://ftp.mozilla.org/pub/firefox/releases/37.0b1/win32/en-US/Firefox%20Setup%2037.0b1.exe",
                             "https://storage.googleapis.com/golang/go1.7.1.windows-amd64.msi");
    }

    @Override
    public boolean saveTest(@RequestBody @NotEmpty JsonNode test) {
        System.out.println(test.asText());
        return true;
    }

    @ExceptionHandler(TestException.class)
    public void handleTestException(TestException testException, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), testException.getMessage());
    }


}
