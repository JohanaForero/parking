package com.forero.parking.application.service;

import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.exception.EmailException;
import com.forero.parking.domain.exception.EntranceException;
import com.forero.parking.domain.exception.ParkingException;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.domain.model.ParkingLot;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void validateExistsParking(final String parkingName) {
        if (this.dbPort.existsParkingName(parkingName)) {
            throw new ParkingException.ParkingNoFoundException("Parking Not found");
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

    public void validateVehicles(final List<History> histories) {
        if (this.isEmptyOrNull(histories)) {
            throw new ParkingException.EmptyList("The parking does not have vehicles");
        }
    }

    public boolean isEmptyOrNull(final List<History> histories) {
        return histories == null || histories.isEmpty();
    }

    public void parkingIsEmpty(final int parkingId) {
        final boolean isParkingInUse = this.dbPort.existsVehiclesInParking(parkingId);
        if (isParkingInUse) {
            throw new ParkingException.ParkingEmpty("The parking is not empty!");
        }
    }

    public void validateUpdateParkingCodeNotLowerThanInUse(final int parkingId, final int numberOfParkingLots) {
        final boolean isParkingCodeNotInUse = this.dbPort.isCodeIsLowerThanCurrent(parkingId, numberOfParkingLots);
        if (isParkingCodeNotInUse) {
            throw new ParkingException.ParkingCodeConflictException("The quota of lots to be updated must not be less" +
                    " than the one currently in use!");
        }
    }

    public void vehicleExistsInTheParking(final int parkingId, final String licensePlate) {

        final boolean isVehicleInParking = this.dbPort.vehicleExistsInTheParkingAtTheMoment(parkingId, licensePlate);
        if (!isVehicleInParking) {
            throw new EmailException.VehicleNotInsideException("The vehicle does not exist in the parking!");
        }
    }
}
