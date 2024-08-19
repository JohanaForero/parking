package com.forero.parking.application.service;

import com.forero.parking.application.port.DbPort;
import com.forero.parking.application.port.EmailServerPort;
import com.forero.parking.infrastructure.adapter.gateways.Email;
import com.forero.parking.infrastructure.util.JwtUtil;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;

@Service
public record EmailService(DbPort dbPort, ValidationService validationService, EmailServerPort emailServerPort) {

    public String sendEmail(final Email email, final String token) {
        final String parkingName = email.getParkingName();
        this.validationService.validateExistsParking(parkingName);
        final int parkingId = this.dbPort.findIdByName(parkingName);
        final boolean isPartner = JwtUtil.isUserPartner(token);
        if (!isPartner) {
            this.validationService.vehicleExistsInTheParking(parkingId, email.getLicensePlate());
            return this.emailServerPort.sendEmail(email);
        }
        final String partnerId = JwtUtil.getClaimFromToken(token, JwtClaimNames.SUB);
        this.validationService.validateParkingBelongsToPartner(parkingId, partnerId);
        this.validationService.vehicleExistsInTheParking(parkingId, email.getLicensePlate());

        return this.emailServerPort.sendEmail(email);
    }
}
