package com.forero.parking.application.service;

import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.exception.EntranceException;
import com.forero.parking.domain.exception.ParkingException;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.domain.model.ParkingLot;
import org.springframework.stereotype.Service;

@Service
public record ValidationService(DbPort dbPort) {
    public void validateParkingLotFree(final ParkingLot parkingLot, final String licensePlate) {
        final boolean isCodeInUse = this.dbPort.thereIsAVehicleInTheParkingLot(parkingLot, licensePlate);
        if (isCodeInUse) {
            throw new EntranceException.OccupiedException(String.format("Parking lot %s is not free", parkingLot.getCode()));
        }
    }

    public void validateThatTheCodeIsNotOccupied(final int parkingId, final int code) {
        final boolean isCodeInUse = this.dbPort.codeIsFree(parkingId, code);
        if (isCodeInUse) {
            throw new EntranceException.OccupiedException(String.format("Parking code %s is not free", code));
        }

    }

    public void validateVehicleInParkingAndCurrentCode(final int parkingId,
                                                       final String licensePlate) {
        final boolean existVehicleInParking = this.dbPort.existsVehicleInParking(parkingId, licensePlate);
        if (existVehicleInParking) {
            throw new EntranceException.VehicleInsideException(String.format("Vehicle with license plate " +
                    "%s is in parking lot %s", licensePlate, parkingId));
        }
    }

    public void validateParkingNameAvailability(final String parkingName) {
        if (!this.dbPort.existsParkingName(parkingName)) {
            throw new ParkingException.ParkingNameAlreadyExistsException((String.format("Parking with name %s it " +
                    "already exists", parkingName)));
        }
    }

    public void validateParkingBelongsToPartner(final int parkingId, final String partnerId) {
        if (this.dbPort.existsParkingByPartnerId(parkingId, partnerId)) {
            throw new ParkingException.UserNoAuthorized("This member is not authorized to access this parking lot.");
        }
    }

    public void validateParkingLotExists(final int code, final int numberOfParkingLots) {
        if (code == 0 || code > numberOfParkingLots) {
            throw new EntranceException.NotFoundParkingLotException(String.format("Parking lot %s not found",
                    code));
        }
    }

    public void validateNameChange(final Parking parking) {
        final boolean currentName = this.dbPort.getCurrentParkingName(parking);
        if (!currentName) {
            this.validateParkingNameAvailability(parking.getName());
        }
    }
}
