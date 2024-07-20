package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    Optional<VehicleEntity> findByLicensePlate(String licensePlate);
}
