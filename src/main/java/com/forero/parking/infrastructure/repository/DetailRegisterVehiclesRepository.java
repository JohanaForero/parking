package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.DetailRegisterVehiclesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetailRegisterVehiclesRepository extends JpaRepository<DetailRegisterVehiclesEntity, Long> {
    Optional<DetailRegisterVehiclesEntity> findByVehicle_LicensePlate(String licensePlate);
}
