package com.forero.parking.application.service;

import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.model.Parking;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public record ParkingService(DbPort dbPort, ValidationService validationService) {

    public int createParking(final Parking parking) {
        this.validationService.validateParkingNameAvailability(parking.getName());
        return this.dbPort.saveParking(parking);
    }

    public List<Parking> getParkings(final String partnerId) {
        List<Parking> result = new ArrayList<>();
        return result;
    }
}
