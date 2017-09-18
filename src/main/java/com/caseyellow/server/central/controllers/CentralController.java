package com.caseyellow.server.central.controllers;

import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.exceptions.ErrorResponse;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.exceptions.InternalException;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.caseyellow.server.central.exceptions.ErrorResponse.INTERNAL_ERROR_CODE;

/**
 * Created by dango on 6/25/17.
 */
@RestController
@RequestMapping("/central")
public class CentralController {

    private TestService centralService;

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

        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());

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
    public void setCentralService(TestService centralService) {
        this.centralService = centralService;
    }
}
