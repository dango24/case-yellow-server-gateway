package caseyellow.server.gateway.controllers;

import caseyellow.server.gateway.domain.Test;
import caseyellow.server.gateway.domain.ErrorResponse;
import caseyellow.server.gateway.domain.interfaces.CentralService;
import caseyellow.server.gateway.exceptions.InternalException;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static caseyellow.server.gateway.domain.ErrorResponse.INTERNAL_ERROR_CODE;

/**
 * Created by dango on 6/25/17.
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private CentralService centralService;

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/next-web-site",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNextSpeedTestWebSite() {

        return centralService.getNextSpeedTestWebSite();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/next-urls",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getNextUrls(@RequestParam("comparison-count") int numOfComparisonPerTest) {

        return centralService.getNextUrls(numOfComparisonPerTest);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/save-test",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveTest(@RequestBody @NotEmpty Test test) {

        centralService.saveTest(test);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponse> handleInternalException(InternalException ex)  {

        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorStatus().getStatusCode(),
                                                        ex.getMessage());

        return ResponseEntity.status(INTERNAL_ERROR_CODE)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex)  {

        return ResponseEntity.status(INTERNAL_ERROR_CODE)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(new ErrorResponse(ex.getMessage()));
    }

    @Autowired
    public void setCentralService(CentralService centralService) {
        this.centralService = centralService;
    }
}
