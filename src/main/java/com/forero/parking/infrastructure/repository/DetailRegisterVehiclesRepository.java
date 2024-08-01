package com.forero.parking.infrastructure.repository;

import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.repository.entity.DetailRegisterVehiclesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetailRegisterVehiclesRepository extends JpaRepository<DetailRegisterVehiclesEntity, Long> {
    Optional<DetailRegisterVehiclesEntity> findByVehicle_LicensePlate(String licensePlate);

    List<DetailRegisterVehiclesEntity> findByVehicleIsNotNull();

    List<Vehicle> findByParkingLot_Name();
}
