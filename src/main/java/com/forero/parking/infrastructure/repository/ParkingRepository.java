package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.ParkingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingRepository extends JpaRepository<ParkingEntity, Long> {
    boolean existsByParkingName(String parkingName);

    List<ParkingEntity> findByPartnerId(String partnerId);

    boolean existsByIdAndPartnerId(int parkingId, String partnerName);
}
