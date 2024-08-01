package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLotEntity, Long> {
    Optional<ParkingLotEntity> findByVehicle_LicensePlate(String licensePlate);
}
