package com.forero.parking.application.port;

import com.forero.parking.infrastructure.adapter.gateways.Email;

public interface EmailServerPort {
    String sendEmail(Email email);
}
