package com.forero.parking.application.port;

import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DbPort {
    ParkingLot registerVehicleEntry(ParkingLot parkingLot, Vehicle vehicle);

    History registerHistoryEntry(ParkingLot parkingLot);

    ParkingLot getParkingLotByLicensePlate(String licensePlate);

    ParkingLot getParkingLotById(long parkingLotId);

    LocalDateTime registerVehicleExit(ParkingLot parkingLot);

    History registerHistoryExit(String licensePlate, long parkingLotId, LocalDateTime entranceDate,
                                LocalDateTime departureDate, BigDecimal totalPaid);
}
