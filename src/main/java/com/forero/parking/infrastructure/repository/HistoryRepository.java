package com.forero.parking.infrastructure.repository;

import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
