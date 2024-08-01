package com.forero.parking.infrastructure.adapter;

import com.forero.parking.application.configuration.TimeConfiguration;
import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.infrastructure.repository.HistoryRepository;
import com.forero.parking.infrastructure.repository.ParkingLotRepository;
import com.forero.parking.infrastructure.repository.VehicleRepository;
import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostGreSqlAdapter implements DbPort {
    private final VehicleRepository vehicleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final HistoryRepository historyRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final HistoryMapper historyMapper;
    private final TimeConfiguration timeConfiguration;

    @Override
    public ParkingLot registerVehicleEntry(final ParkingLot parkingLot, final Vehicle vehicle) {
        final VehicleEntity vehicleEntity = this.vehicleRepository.findByLicensePlate(vehicle.getLicensePlate())
                .orElseGet(() -> {
                    final VehicleEntity newVehicleEntity = new VehicleEntity();
                    newVehicleEntity.setLicensePlate(vehicle.getLicensePlate());
                    return this.vehicleRepository.save(newVehicleEntity);
                });
        ParkingLotEntity parkingLotEntity = this.parkingLotRepository.findById(parkingLot.getId())
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
        final ParkingLotEntity parkingLotEntity = this.parkingLotRepository.findByVehicleLicensePlate(licensePlate)
                .orElse(null);
        return this.parkingLotMapper.toDomain(parkingLotEntity);
    }

    @Override
    public ParkingLot getParkingLotById(final long parkingLotId) {
        final ParkingLotEntity parkingLotEntity = this.parkingLotRepository.findById(parkingLotId)
                .orElse(null);
        return this.parkingLotMapper.toDomain(parkingLotEntity);
    }

    @Override
    public LocalDateTime registerVehicleExit(final ParkingLot parkingLot) {
        final ParkingLotEntity parkingLotEntity = this.parkingLotRepository.findById(parkingLot.getId())
                .orElse(new ParkingLotEntity());

        final LocalDateTime entranceDate = parkingLotEntity.getEntranceDate();

        parkingLotEntity.setVehicle(null);
        parkingLotEntity.setEntranceDate(null);
        this.parkingLotRepository.save(parkingLotEntity);

        return entranceDate;
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
    public List<ParkingLot> getVehiclesInParking() {
        return this.parkingLotRepository.findByVehicleIsNotNull()
                .stream()
                .map(this.parkingLotMapper::toDomain)
                .toList();
    }

    @Override
    public int save(ParkingLot parkingLot) {
        ParkingLotEntity parkingLotEntity = this.parkingLotMapper.toEntity(parkingLot);
        ParkingLotEntity entity = this.parkingLotRepository.save(parkingLotEntity);
        return entity.getId().intValue();
    }
}
