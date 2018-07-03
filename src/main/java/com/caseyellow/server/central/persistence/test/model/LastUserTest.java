package com.caseyellow.server.central.persistence.test.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static com.caseyellow.server.central.domain.mail.EmailServiceImpl.DATE_FORMAT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LastUserTest {

    private String user;
    private long timestamp;
    private String phone;

    public LastUserTest(String user, long timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        String phoneNumber = null;

        if (StringUtils.isNotEmpty(phone)) {
            phoneNumber = String.format("%s-%s", phone.substring(0, 3), phone.substring(3));
        }

        return String.format("%s last test: %s, phone: %s", user, DATE_FORMAT.format(timestamp), phoneNumber);
    }
}
