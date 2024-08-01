package com.forero.parking.infrastructure.adapter;

import com.forero.parking.application.configuration.TimeConfiguration;
import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.mapper.DetailRegisterVehicleMapper;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.infrastructure.repository.DetailRegisterVehiclesRepository;
import com.forero.parking.infrastructure.repository.HistoryRepository;
import com.forero.parking.infrastructure.repository.ParkingLotRepository;
import com.forero.parking.infrastructure.repository.VehicleRepository;
import com.forero.parking.infrastructure.repository.entity.DetailRegisterVehiclesEntity;
import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class PostGreSqlAdapter implements DbPort {
    private final VehicleRepository vehicleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final DetailRegisterVehiclesRepository detailRegisterVehiclesRepository;
    private final HistoryRepository historyRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final HistoryMapper historyMapper;
    private final TimeConfiguration timeConfiguration;
    private final DetailRegisterVehicleMapper detailRegisterVehicleMapper;

    @Override
    public ParkingLot registerVehicleEntry(final ParkingLot parkingLot) {
        final String licensePlate = parkingLot.getVehicle().getLicensePlate();
        final VehicleEntity vehicleEntity = this.vehicleRepository.findByLicensePlate(licensePlate)
                .orElseGet(() -> {
                    final VehicleEntity newVehicleEntity = new VehicleEntity();
                    newVehicleEntity.setLicensePlate(licensePlate);
                    return this.vehicleRepository.save(newVehicleEntity);
                });
        DetailRegisterVehiclesEntity parkingLotEntity = this.parkingLotRepository.findById(parkingLot.getId())
                .orElseGet(() -> {
                    final ParkingLotEntity newParkingLotEntity = new ParkingLotEntity();
                    newParkingLotEntity.setId(parkingLot.getId());
                    return newParkingLotEntity;
                });

        final LocalDateTime entranceDate = this.timeConfiguration.now();

        parkingLotEntity.setVehicle(vehicleEntity);
        parkingLotEntity.setEntranceDate(entranceDate);
        parkingLotEntity = this.parkingLotRepository.save(parkingLotEntity);

        return this.parkingLotMapper.toDomain(parkingLotEntity);
    }

    @Override
    public History registerHistoryEntry(final ParkingLot parkingLot) {
        final VehicleEntity vehicleEntity = this.vehicleRepository.findById(parkingLot.getVehicle().getId())
                .orElse(null);
        final ParkingLotEntity parkingLotEntity = this.parkingLotRepository.findById(parkingLot.getId())
                .orElse(null);

        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setVehicle(vehicleEntity);
        historyEntity.setParkingLot(parkingLotEntity);
        historyEntity.setEntranceDate(parkingLot.getEntranceDate());
        historyEntity = this.historyRepository.save(historyEntity);

        return this.historyMapper.toDomain(historyEntity);
    }

    @Override
    public ParkingLot getParkingLotByLicensePlate(final String licensePlate) {
        final ParkingLotEntity parkingLotEntity = this.parkingLotRepository.findByVehicle_LicensePlate(licensePlate)
                .orElse(null);
        return this.parkingLotMapper.toDomain(parkingLotEntity);
    }

    @Override
    public ParkingLot getParkingLotById(final long parkingLotId) {
        final ParkingLotEntity parkingLotEntity = this.parkingLotRepository.findById(parkingLotId)
                .orElse(null);
        return this.detailRegisterVehicleMapper.toDomain(parkingLotEntity);
    }

    @Override
    public LocalDateTime registerVehicleExit(final ParkingLot parkingLot) {
        final DetailRegisterVehiclesEntity detailRegisterVehiclesEntity =
                this.getDetailByVehicleLicensePlate(parkingLot.getVehicle());
        final LocalDateTime entranceDate = detailRegisterVehiclesEntity.getEntranceDate();

        detailRegisterVehiclesEntity.setVehicle(null);
        detailRegisterVehiclesEntity.setEntranceDate(null);
        this.detailRegisterVehiclesRepository.save(detailRegisterVehiclesEntity);

        return entranceDate;
    }


    private DetailRegisterVehiclesEntity getDetailByVehicleLicensePlate(final Vehicle vehicle) {
        return this.detailRegisterVehiclesRepository.findByVehicle_LicensePlate(vehicle.getLicensePlate())
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public History registerHistoryExit(final String licensePlate, final long parkingLotId,
                                       final LocalDateTime entranceDate, final LocalDateTime departureDate,
                                       final BigDecimal totalPaid) {
        return this.historyRepository.findByParkingLotIdAndVehicleLicensePlateAndEntranceDateAndDepartureDateIsNull(parkingLotId, licensePlate, entranceDate)
                .map(historyEntity -> {
                    historyEntity.setDepartureDate(departureDate);
                    historyEntity.setTotalPaid(totalPaid);
                    historyEntity = this.historyRepository.save(historyEntity);
                    return this.historyMapper.toDomain(historyEntity);
                })
                .orElse(null);
    }

    @Override
    public Long save(ParkingLot parkingLot) {
        ParkingLotEntity parkingLotEntity = this.parkingLotMapper.toEntity(parkingLot);
        ParkingLotEntity entity = this.parkingLotRepository.save(parkingLotEntity);
        return entity.getId();
    }
}
