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

    @Query("SELECT h " +
            "FROM HistoryEntity h " +
            "JOIN h.parkingLot pl " +
            "JOIN pl.parking p " +
            "WHERE p.id = :parkingId " +
            "AND h.vehicle.id IN (" +
            "   SELECT h2.vehicle.id " +
            "   FROM HistoryEntity h2 " +
            "   JOIN h2.parkingLot pl2 " +
            "   JOIN pl2.parking p2 " +
            "   WHERE p2.id = :parkingId " +
            "   GROUP BY h2.vehicle.id " +
            "   ORDER BY COUNT(h2.id) DESC" +
            ") " +
            "GROUP BY h.vehicle.id, h.id, h.entranceDate, h.departureDate, h.totalPaid, pl.id " +
            "ORDER BY COUNT(h.id) DESC")
    List<HistoryEntity> findTop10HistoriesByTopVehiclesInParking(@Param("parkingId") long parkingId);


//    @Query("SELECT h " +
//            "FROM HistoryEntity h " +
//            "JOIN h.parkingLot pl " +
//            "JOIN pl.parking p " +
//            "WHERE p.id = :parkingId " +
//            "AND pl.vehicle.id = :vehicleId " +
//            "ORDER BY h.entranceDate DESC")
//    Optional<HistoryEntity> findTopByVehicleAndParkingId(@Param("vehicleId") long vehicleId,
//                                                         @Param("parkingId") int parkingId);

}


