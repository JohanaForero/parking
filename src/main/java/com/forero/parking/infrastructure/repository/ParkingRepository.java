package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.ParkingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingRepository extends JpaRepository<ParkingEntity, Long> {
    boolean existsByParkingName(String parkingName);

    List<ParkingEntity> findByPartnerId(String partnerId);

    boolean existsByIdAndPartnerId(int parkingId, String partnerName);

    boolean existsByIdAndParkingName(Long id, String parkingName);

    @Query("SELECT p.id FROM ParkingEntity p WHERE p.parkingName = :parkingName")
    Long findIdByParkingName(@Param("parkingName") String parkingName);
}
