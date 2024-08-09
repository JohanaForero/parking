package com.forero.parking.application.service;

import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.exception.DepartureException;
import com.forero.parking.domain.exception.EntranceException;
import com.forero.parking.domain.exception.ParkingException;
import com.forero.parking.domain.model.ParkingLot;
import org.springframework.stereotype.Service;

@Service
public record ValidationService(DbPort dbPort) {
    public void validateParkingLotFree(final ParkingLot parkingLot, final String licensePlate) {
        final boolean isCodeInUse = this.dbPort.ThereIsAVehicleInTheParkingLot(parkingLot, licensePlate);
        if (isCodeInUse) {
            throw new EntranceException.OccupiedException(String.format("Parking lot %s is not free", parkingLot.getCode()));
        }
    }

//    public void validateVehicleNotInside(final String licensePlate, final int parkingId) {
//        final boolean existsPlateInParking = this.doesLicensePlateExistInParking(licensePlate, parkingId);
//        if (existsPlateInParking) {
//            throw new EntranceException.VehicleInsideException(String.format("Vehicle with license plate %s is " +
//                    "already inside in parking %s", licensePlate, parkingId));
//        }
//    }

    private boolean doesLicensePlateExistInParking(final String licensePlate, final int parkingId) {
        return this.dbPort.thereIsAPlaqueInTheParking(licensePlate, parkingId);
    }

//    public void validateVehicleInside(final String licensePlate) {
//        final ParkingLot parkingLot = this.dbPort.getParkingLotByLicensePlate(licensePlate);
//        if (parkingLot == null) {
//            throw new EmailException.VehicleNotInsideException(String.format("Vehicle with license plate %s is " +
//                    "not inside in parking", licensePlate));
//        }
//    }

    public void validateVehicleInParkingAndCode(final int parkingLotId, final int code, final String licensePlate) {
        final ParkingLot parkingLot = this.dbPort.getParkingLotByLicensePlateAndCodeAndParking(parkingLotId, code,
                licensePlate);
        if (parkingLot == null) {
            throw new DepartureException.VehicleNotInParkingLotException(String.format("Vehicle with license plate " +
                    "%s is not in parking lot %s", licensePlate, parkingLotId));
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
            throw new EntranceException.NotFoundParkingException("This member is not authorized to access this parking lot.");
        }
    }

    public void validateParkingLotExists(final int code, final int numberOfParkingLots) {
        if (code == 0 || code > numberOfParkingLots) {
            throw new EntranceException.NotFoundParkingLotException(String.format("Parking lot %s not found",
                    code));
        }
    }
}
