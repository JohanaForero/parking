package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

    Optional<HistoryEntity> findByParkingLotIdAndVehicleLicensePlateAndEntranceDateAndDepartureDateIsNull(long parkingLotId,
                                                                                                          String licensePlate,
                                                                                                          LocalDateTime entranceDate);

    @Query("SELECT h " +
            "FROM HistoryEntity h " +
            "WHERE h.parkingLot.parking.id = :parkingId " +
            "AND h.parkingLot.code = :code " +
            "AND h.vehicle.licensePlate = :licensePlate " +
            "AND h.departureDate IS NULL " +
            "AND h.totalPaid IS NULL")
    Optional<HistoryEntity> findByParkingLotIdAndVehicleLicensePlateAndDepartureDateIsNull(
            @Param("parkingId") long parkingId,
            @Param("code") int code,
            @Param("licensePlate") String licensePlate);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
            "FROM HistoryEntity h " +
            "WHERE h.parkingLot.parking.id = :parkingId " +
            "AND h.parkingLot.code = :code " +
            "AND h.vehicle.licensePlate = :licensePlate " +
            "AND h.departureDate IS NULL " +
            "AND h.totalPaid IS NULL")
    boolean existsByParkingLotAndVehicleAndNoDepartureOrPayment(
            @Param("parkingId") int parkingId,
            @Param("code") int code,
            @Param("licensePlate") String licensePlate);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
            "FROM HistoryEntity h " +
            "WHERE h.parkingLot.parking.id = :parkingId " +
            "AND h.vehicle.licensePlate = :licensePlate " +
            "AND h.departureDate IS NULL " +
            "AND h.totalPaid IS NULL")
    boolean existsByParkingAndVehicleAndNoDepartureOrPayment(
            @Param("parkingId") int parkingId,
            @Param("licensePlate") String licensePlate);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
            "FROM HistoryEntity h " +
            "WHERE h.parkingLot.parking.id = :parkingId " +
            "AND h.parkingLot.code = :code " +
            "AND h.departureDate IS NULL " +
            "AND h.totalPaid IS NULL")
    boolean isCodeOccupied(
            @Param("parkingId") int parkingId,
            @Param("code") int code);


}
