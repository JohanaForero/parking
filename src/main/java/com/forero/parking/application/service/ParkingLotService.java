package com.forero.parking.application.service;

import com.forero.parking.application.configuration.TimeConfiguration;
import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public record ParkingLotService(DbPort dbPort, ValidationService validationService,
                                TimeConfiguration timeConfiguration) {

    public History registerVehicleEntry(ParkingLot parkingLot, final String partnerId, final String licensePlate) {
        final int parkingId = parkingLot.getParkingId().intValue();
        final int numberOfParkingLots = this.dbPort.getNumberOfParkingLots(parkingId);
        this.validationService.validateParkingBelongsToPartner(parkingId, partnerId);
        this.validationService.validateParkingLotExists(parkingLot.getCode(), numberOfParkingLots);
        this.validationService.validateParkingLotFree(parkingLot, licensePlate);
//        this.validationService.validateVehicleNotInside(licensePlate, parkingId);

        parkingLot = this.dbPort.registerVehicleEntry(parkingLot, licensePlate);
        return this.dbPort.registerHistoryEntry(parkingLot, licensePlate);
    }

    public History registerVehicleExit(final ParkingLot parkingLot) {
        final Vehicle vehicle = new Vehicle();
        final ParkingLot parkingLotResult =
                this.dbPort.getParkingLotByLicensePlateAndCodeAndParking(parkingLot.getParkingId().intValue(),
                        parkingLot.getCode(), vehicle.getLicensePlate());
        this.validationService.validateVehicleInParkingAndCode(parkingLot.getParkingId().intValue(),
                parkingLot.getCode(), vehicle.getLicensePlate());

        final Parking parking = this.dbPort.findById(parkingLot.getParkingId());
        final LocalDateTime departureDate = this.timeConfiguration.now();
        final LocalDateTime entranceDate = this.dbPort.registerVehicleExit(parkingLotResult);

        final BigDecimal totalPaid = this.calculateTotalPaid(entranceDate, departureDate, parking);

        return this.dbPort.registerHistoryExit(vehicle.getLicensePlate(), parkingLotResult.getId(), entranceDate,
                departureDate, totalPaid);
    }

//    public List<ParkingLot> getVehiclesInParking() {
//        return this.dbPort.getVehiclesInParking();
//    }

    private BigDecimal calculateTotalPaid(final LocalDateTime entranceDate, final LocalDateTime departureDate,
                                          final Parking parking) {
        final Duration duration = Duration.between(entranceDate, departureDate);
        final long minutes = duration.toSeconds();
        final double hoursRounded = Math.ceil(minutes / 3600.0);
        return this.getCostPerHour(parking).multiply(BigDecimal.valueOf(hoursRounded));
    }

    private BigDecimal getCostPerHour(final Parking parking) {
        final int costPerHour = parking.getCostPerHour();
        return BigDecimal.valueOf(costPerHour);
    }
}
