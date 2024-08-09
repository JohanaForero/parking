package com.forero.parking.application.port;

import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.domain.model.ParkingLot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DbPort {
    ParkingLot registerVehicleEntry(ParkingLot parkingLot);

    History registerHistoryEntry(ParkingLot parkingLot);

    ParkingLot getParkingLotByLicensePlate(String licensePlate);

    LocalDateTime registerVehicleExit(ParkingLot parkingLot);

    History registerHistoryExit(String licensePlate, long parkingLotId, LocalDateTime entranceDate,
                                LocalDateTime departureDate, BigDecimal totalPaid);

    List<ParkingLot> getVehiclesInParking();

    int saveParking(Parking parking);

    boolean existsParkingName(String parkingName);

    List<Parking> findAllParking(String partnerId);

    int getNumberOfParkingLots(int parkingId);

    boolean existsParkingByPartnerId(int parkingId, String partnerId);

    boolean thereIsAPlaqueInTheParking(String licensePlate, int parkingId);

    boolean existsCodeInParking(long parkingId, int code);

    ParkingLot getParkingLotByLicensePlateAndCodeAndParking(int parkingId, int code, String licensePlate);

    Parking findById(long parkingId);
}
