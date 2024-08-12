package com.forero.parking.application.service;

import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.util.JwtUtil;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record ParkingService(DbPort dbPort, ValidationService validationService) {

    public int createParking(final Parking parking) {
        this.validationService.validateParkingNameAvailability(parking.getName());
        return this.dbPort.saveParking(parking);
    }

    public List<Parking> getParkings(final String token) {
        final boolean isPartner = JwtUtil.isUserPartner(token);
        if (!isPartner) {
            return this.dbPort.getAllParkings();
        }
        final String partnerId = JwtUtil.getClaimFromToken(token, JwtClaimNames.SUB);
        return this.dbPort.findAllParking(partnerId);
    }

    public Parking getParking(final int idParking, final String token) {
        final boolean isPartner = JwtUtil.isUserPartner(token);
        if (!isPartner) {
            return this.dbPort.findById(idParking);
        }

        final String partnerId = JwtUtil.getClaimFromToken(token, JwtClaimNames.SUB);
        this.validationService.validateParkingBelongsToPartner(idParking, partnerId);
        return this.dbPort.findById(idParking);
    }

    public void deleteParking(final int idParking) {
        this.dbPort.deleteParking(idParking);
    }

    public void updatePartially(final Parking parking) {
        this.dbPort.updatePartially(parking);
    }

    public void updateParking(final Parking parking) {
        this.validationService.validateNameChange(parking);
        this.dbPort.updateParking(parking);
    }
}
