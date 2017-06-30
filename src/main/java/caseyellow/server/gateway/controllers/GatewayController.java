package caseyellow.server.gateway.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by dango on 6/25/17.
 */
public interface GatewayController {

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/next-web-site",
                    method = RequestMethod.GET,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    String getNextSpeedTestWebSite();

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/next-urls",
                    method = RequestMethod.GET,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> getNextUrls(@RequestParam("comparison-count") int numOfComparisonPerTest);

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/save-test",
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    boolean saveTest(@RequestBody @NotEmpty JsonNode test);

}
