package com.forero.parking.application.service;

import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.exception.DepartureException;
import com.forero.parking.domain.exception.EmailException;
import com.forero.parking.domain.exception.EntranceException;
import com.forero.parking.domain.exception.ParkingException;
import com.forero.parking.domain.model.ParkingLot;
import org.springframework.stereotype.Service;

@Service
public record ValidationService(DbPort dbPort) {
    public void validateParkingLotExists(final long parkingLotId, final int numberOfParkingLots) {
        if (parkingLotId == 0 || parkingLotId > numberOfParkingLots) {
            throw new EntranceException.NotFoundParkingLotException(String.format("Parking lot %s not found",
                    parkingLotId));
        }
    }

    public void validateParkingLotFree(final long parkingLotId) {
        final ParkingLot parkingLot = this.dbPort.getParkingLotById(parkingLotId);
        if (parkingLot != null && parkingLot.getVehicle() != null) {
            throw new EntranceException.OccupiedException(String.format("Parking lot %s is not free",
                    parkingLot.getId()));
        }
    }

    public void validateVehicleNotInside(final String licensePlate, final Long parkingId) {
        final boolean existePlacaEnParking = this.ThereIsAPlaqueInTheParking(licensePlate, parkingId);
        final ParkingLot parkingLot = this.dbPort.getParkingLotByLicensePlate(licensePlate);
        if (parkingLot != null) {
            throw new EntranceException.VehicleInsideException(String.format("Vehicle with license plate %s is " +
                    "already inside in parking lot %s", licensePlate, parkingLot.getId()));
        }
    }

    private boolean ThereIsAPlaqueInTheParking(final String licensePlate, final Long parkingId) {
        final boolean result = this.dbPort.thereIsAPlaqueInTheParking(licensePlate, parkingId);
        return !result;
    }

    public void validateVehicleInside(final String licensePlate) {
        final ParkingLot parkingLot = this.dbPort.getParkingLotByLicensePlate(licensePlate);
        if (parkingLot == null) {
            throw new EmailException.VehicleNotInsideException(String.format("Vehicle with license plate %s is " +
                    "not inside in parking", licensePlate));
        }
    }

    public void validateVehicleInParkingLot(final String licensePlate, final long parkingLotId) {
        final ParkingLot parkingLot = this.dbPort.getParkingLotByLicensePlate(licensePlate);
        if (parkingLot == null || parkingLot.getId() != parkingLotId) {
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

    public void validarSiElParkingLotEStaEnParking(final int parkingLotId, final int numberOfParkingLots) {
        if (this.validarCupo(parkingLotId, numberOfParkingLots)) {
            throw new EntranceException.NotFoundParkingLotInParkingException((String.format("No tenemos ese espacio " +
                    "de parqueadero solo tenemos disponibles hasta el %s", numberOfParkingLots)));
        }
    }

    private boolean validarCupo(final int parkingLotId, final int numberOfParkingLots) {
        return parkingLotId > 0 && parkingLotId <= numberOfParkingLots;
    }
}
