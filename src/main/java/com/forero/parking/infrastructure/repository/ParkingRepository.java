package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.ParkingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<ParkingEntity, Long> {
}
