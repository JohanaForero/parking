package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

    Optional<HistoryEntity> findByParkingLotIdAndVehicleLicensePlateAndEntranceDateAndDepartureDateIsNull(long parkingLotId,
                                                                                                          String licensePlate,
                                                                                                          LocalDateTime entranceDate);

    Optional<HistoryEntity> findByParkingLotParkingIdAndParkingLotCodeAndVehicleLicensePlateAndDepartureDateIsNullAndTotalPaidIsNull(
            long parkingId, int code, String licensePlate);


    boolean existsByParkingLotParkingIdAndParkingLotCodeAndVehicleLicensePlateAndDepartureDateIsNullAndTotalPaidIsNull(
            int parkingId, int code, String licensePlate);

    boolean existsByParkingLotParkingIdAndVehicleLicensePlateAndDepartureDateIsNullAndTotalPaidIsNull(int parkingId,
                                                                                                      String licensePlate);

    boolean existsByParkingLotParkingIdAndParkingLotCodeAndDepartureDateIsNullAndTotalPaidIsNull(int parkingId,
                                                                                                 int code);

    List<HistoryEntity> findByParkingLotId(long parkingLot);

    @Query("SELECT COUNT(h) " +
            "FROM HistoryEntity h " +
            "JOIN h.parkingLot pl " +
            "JOIN pl.parking p " +
            "WHERE p.id = :parkingId " +
            "AND h.departureDate IS NULL " +
            "AND h.totalPaid IS NULL")
    Long countVehiclesInParking(@Param("parkingId") int parkingId);

    @Query("SELECT h " +
            "FROM HistoryEntity h " +
            "JOIN h.parkingLot pl " +
            "JOIN pl.parking p " +
            "WHERE p.id = :parkingId " +
            "AND h.departureDate IS NULL " +
            "AND h.totalPaid IS NULL")
    Page<HistoryEntity> findActiveHistoriesByParkingId(@Param("parkingId") int parkingId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
            "FROM HistoryEntity h " +
            "JOIN h.parkingLot pl " +
            "JOIN pl.parking p " +
            "WHERE p.id = :parkingId " +
            "AND h.departureDate IS NULL " +
            "AND h.totalPaid IS NULL " +
            "AND pl.code > :code")
    boolean existsActiveParkingLotWithHigherCode(@Param("parkingId") long parkingId, @Param("code") int code);
}

