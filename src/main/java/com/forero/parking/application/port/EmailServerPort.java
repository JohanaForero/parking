package com.forero.parking.application.port;

import com.forero.parking.domain.model.Email;

public interface EmailServerPort {
    String sendEmail(Email email);
}
