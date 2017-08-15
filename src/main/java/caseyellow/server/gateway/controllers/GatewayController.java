package caseyellow.server.gateway.controllers;

import caseyellow.server.gateway.domain.model.Test;
import caseyellow.server.gateway.enums.ErrorResponse;
import caseyellow.server.gateway.exceptions.InternalException;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static caseyellow.server.gateway.enums.ErrorResponse.INTERNAL_ERROR_CODE;

/**
 * Created by dango on 6/25/17.
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/next-web-site", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNextSpeedTestWebSite() {
        return "ookla";
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/next-urls", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getNextUrls(@RequestParam("comparison-count") int numOfComparisonPerTest) {

        return Arrays.asList("http://mirrors.kodi.tv/releases/osx/x86_64/kodi-17.3-Krypton-x86_64.dmg",
                             "https://ftp.mozilla.org/pub/firefox/releases/37.0b1/win32/en-US/Firefox%20Setup%2037.0b1.exe",
                             "https://storage.googleapis.com/golang/go1.7.1.windows-amd64.msi");
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/save-test",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveTest(@RequestBody @NotEmpty Test test) {
        System.out.println(test.toString());
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponse> handleInternalException(InternalException ex)  {

        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorStatus().getStatusCode(),
                                                        ex.getMessage());

        return ResponseEntity.status(INTERNAL_ERROR_CODE)
                             .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                             .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex)  {

        return ResponseEntity.status(INTERNAL_ERROR_CODE)
                             .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                             .body(new ErrorResponse(ex.getMessage()));
    }

}
