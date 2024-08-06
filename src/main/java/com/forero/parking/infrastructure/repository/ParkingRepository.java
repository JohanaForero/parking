package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.ParkingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingRepository extends JpaRepository<ParkingEntity, Long> {
    boolean existsByParkingName(String parkingName);

    List<ParkingEntity> findByPartnerId(String partnerId);

    Optional<ParkingEntity> findByParkingName(String parkingName);

    boolean existsByIdAndPartnerId(int parkingId, String partnerName);
}
