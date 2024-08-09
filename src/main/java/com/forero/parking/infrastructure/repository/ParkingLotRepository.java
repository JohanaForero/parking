package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLotEntity, Long> {
    Optional<ParkingLotEntity> findByVehicleLicensePlate(String licensePlate);

    List<ParkingLotEntity> findByVehicleIsNotNull();

    boolean existsByParking_IdAndVehicle_LicensePlate(int parkingId, String licensePlate);

    boolean existsByParkingIdAndCode(long parkingId, int code);

    @Query("SELECT p FROM ParkingLotEntity p WHERE p.parking.id = :parkingId AND p.code = :code AND p.vehicle.licensePlate = :licensePlate")
    Optional<ParkingLotEntity> findParkingLotByParkingIdAndCodeAndLicensePlate(@Param("parkingId") Long parkingId,
                                                                               @Param("code") int code,
                                                                               @Param("licensePlate") String licensePlate);
}
