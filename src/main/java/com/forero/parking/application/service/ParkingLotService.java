package com.forero.parking.application.service;

import com.forero.parking.application.configuration.GlobalConfiguration;
import com.forero.parking.application.configuration.TimeConfiguration;
import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public record ParkingLotService(DbPort dbPort, ValidationService validationService,
                                GlobalConfiguration globalConfiguration, TimeConfiguration timeConfiguration) {

    public History registerVehicleEntry(ParkingLot parkingLot, final Vehicle vehicle) {
        this.validationService.validateParkingLotExists(parkingLot.getId());
        this.validationService.validateParkingLotFree(parkingLot.getId());
        this.validationService.validateVehicleNotInside(vehicle.getLicensePlate());

        parkingLot = this.dbPort.registerVehicleEntry(parkingLot, vehicle);
        return this.dbPort.registerHistoryEntry(parkingLot);
    }

    public History registerVehicleExit(final ParkingLot parkingLot, final Vehicle vehicle) {
        this.validationService.validateVehicleInParkingLot(vehicle.getLicensePlate(), parkingLot.getId());

        final LocalDateTime entranceDate = this.dbPort.registerVehicleExit(parkingLot);

        final LocalDateTime departureDate = this.timeConfiguration.now();

        final BigDecimal totalPaid = this.calculateTotalPaid(entranceDate, departureDate);

        return this.dbPort.registerHistoryExit(vehicle.getLicensePlate(), parkingLot.getId(), entranceDate,
                departureDate, totalPaid);
    }

    public List<ParkingLot> getVehiclesInParking() {
        return this.dbPort.getVehiclesInParking();
    }

    private BigDecimal calculateTotalPaid(final LocalDateTime entranceDate, final LocalDateTime departureDate) {
        final Duration duration = Duration.between(entranceDate, departureDate);
        final long minutes = duration.toSeconds();
        final double hoursRounded = Math.ceil(minutes / 3600.0);
        return this.globalConfiguration.getCostPerHour().multiply(BigDecimal.valueOf(hoursRounded));
    }
}
