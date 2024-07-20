package com.forero.parking.application.service;

import com.forero.parking.application.port.EmailServerPort;
import com.forero.parking.domain.model.Email;
import org.springframework.stereotype.Service;

@Service
public record EmailService(ValidationService validationService, EmailServerPort emailServerPort) {

    public String sendEmail(final Email email) {
        this.validationService.validateVehicleInside(email.getLicensePlate());

        return this.emailServerPort.sendEmail(email);
    }
}
