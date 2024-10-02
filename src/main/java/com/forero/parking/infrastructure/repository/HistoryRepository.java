package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
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

    @Query("SELECT h.vehicle " +
            "FROM HistoryEntity h " +
            "JOIN h.parkingLot pl " +
            "JOIN pl.parking p " +
            "WHERE p.id = :parkingId " +
            "GROUP BY h.vehicle " +
            "ORDER BY COUNT(h.id) DESC, " +
            "SUM(CASE WHEN h.departureDate IS NULL THEN CURRENT_TIMESTAMP - h.entranceDate ELSE h.departureDate - h.entranceDate END) DESC")
    List<VehicleEntity> findTop10VehiclesByEntriesAndDurationInParking(@Param("parkingId") long parkingId, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT h.id) " +
            "FROM HistoryEntity h " +
            "JOIN h.parkingLot pl " +
            "JOIN pl.parking p " +
            "WHERE p.id = :parkingId " +
            "AND h.vehicle.id = :vehicleId")
    int countEntriesByVehicleInParking(@Param("parkingId") long parkingId, @Param("vehicleId") long vehicleId);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN TRUE ELSE FALSE END " +
            "FROM HistoryEntity h " +
            "WHERE h.vehicle.licensePlate = :licensePlate " +
            "AND h.parkingLot.parking.id = :parkingId " +
            "AND h.departureDate IS NULL " +
            "AND h.totalPaid IS NULL")
    boolean isVehicleInParking(@Param("licensePlate") String licensePlate, @Param("parkingId") Long parkingId);


    @Query("SELECT h.vehicle AS vehicle, COUNT(h.id) AS visitCount " +
            "FROM HistoryEntity h " +
            "GROUP BY h.vehicle " +
            "ORDER BY COUNT(h.id) DESC")
    Page<Object[]> findTop10VehiclesByTotalEntries(Pageable pageable);

    @Query("SELECT h.vehicle FROM HistoryEntity h WHERE h.parkingLot.parking.id = :parkingId AND h.departureDate IS NULL AND h.totalPaid IS NULL")
    List<VehicleEntity> findVehiclesCurrentlyParkedInParking(@Param("parkingId") int parkingId);

    @Query("SELECT COUNT(h) FROM HistoryEntity h WHERE h.vehicle.id = :vehicleId AND h.parkingLot.parking.id = :parkingId")
    int countVehicleEntriesInParking(@Param("vehicleId") Long vehicleId, @Param("parkingId") Long parkingId);


}


