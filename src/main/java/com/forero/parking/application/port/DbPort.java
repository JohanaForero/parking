package com.forero.parking.application.port;

import com.forero.parking.domain.agregate.Pagination;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DbPort {
    ParkingLot registerVehicleEntry(ParkingLot parkingLot, String licensePlate);

    History registerHistoryEntry(ParkingLot parkingLot, String licensePlate);

    History registerHistoryExit(String licensePlate, long parkingLotId, LocalDateTime entranceDate,
                                LocalDateTime departureDate, BigDecimal totalPaid);

    boolean thereIsAVehicleInTheParkingLot(ParkingLot parkingLot, String licensePlate);

    boolean codeIsFree(int parkingId, int code);

    int saveParking(Parking parking);

    boolean existsParkingName(String parkingName);

    List<Parking> findAllParking(String partnerId);

    List<Parking> getAllParkings();

    int getNumberOfParkingLots(int parkingId);

    boolean existsParkingByPartnerId(int parkingId, String partnerId);

    Vehicle getVehicle(String licensePlate);

    Parking findById(long parkingId);

    boolean existsVehicleInParking(int parkingId, String licensePlate);

    ParkingLot getParkingLotByCodeAndParkingEntry(int code, int parkingId, String licensePlate);

    void deleteParking(int parkingId);

    void updatePartially(Parking parking);

    void updateParking(Parking parking);

    boolean getCurrentParkingName(Parking parking);

    int getTotalVehicles(int parkingId);

    List<History> getVehicles(int parkingId, Pagination pagination);

}
