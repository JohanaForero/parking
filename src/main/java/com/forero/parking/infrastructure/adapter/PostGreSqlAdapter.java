package com.forero.parking.infrastructure.adapter;

import com.forero.parking.application.configuration.TimeConfiguration;
import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.mapper.DetailRegisterVehicleMapper;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.infrastructure.mapper.VehicleMapper;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostGreSqlAdapter implements DbPort {
    private final VehicleRepository vehicleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final DetailRegisterVehiclesRepository detailRegisterVehiclesRepository;
    private final HistoryRepository historyRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final VehicleMapper vehicleMapper;
    private final HistoryMapper historyMapper;
    private final TimeConfiguration timeConfiguration;
    private final DetailRegisterVehicleMapper detailRegisterVehicleMapper;

    @Override
    public ParkingLot registerVehicleEntry(final ParkingLot parkingLot, final Vehicle vehicle) {
        final VehicleEntity vehicleEntity = this.vehicleRepository.findByLicensePlate(vehicle.getLicensePlate())
                .orElseGet(() -> {
                    final VehicleEntity newVehicleEntity = new VehicleEntity();
                    newVehicleEntity.setLicensePlate(vehicle.getLicensePlate());
                    return this.vehicleRepository.save(newVehicleEntity);
                });
        final ParkingLotEntity parkingLotEntity = this.parkingLotRepository.findById(parkingLot.getId())
                .orElseThrow(RuntimeException::new);
        final DetailRegisterVehiclesEntity detailRegisterVehiclesEntity = new DetailRegisterVehiclesEntity();
        detailRegisterVehiclesEntity.setParkingLot(parkingLotEntity);
        detailRegisterVehiclesEntity.setVehicle(vehicleEntity);
        final LocalDateTime entranceDate = this.timeConfiguration.now();
        detailRegisterVehiclesEntity.setEntranceDate(entranceDate);

        final DetailRegisterVehiclesEntity detail =
                this.detailRegisterVehiclesRepository.save(detailRegisterVehiclesEntity);
        Vehicle vehicle1 = this.vehicleMapper.toModel(detail.getVehicle());

        return ParkingLot.builder()
                .id(detail.getId())
                .vehicle(vehicle1)
                .entranceDate(detail.getEntranceDate())
                .build();
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
        final Optional<DetailRegisterVehiclesEntity> detailRegisterVehiclesEntityOptional =
                this.detailRegisterVehiclesRepository.findByVehicle_LicensePlate(licensePlate);

        if (detailRegisterVehiclesEntityOptional.isEmpty()) {
            throw new RuntimeException("Parking lot not found for license plate: " + licensePlate);
        }
        final DetailRegisterVehiclesEntity detailRegisterVehiclesEntity = detailRegisterVehiclesEntityOptional.get();
        return this.parkingLotMapper.toModel(detailRegisterVehiclesEntity.getParkingLot());
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

//    @Override
//    public List<Vehicle> getVehiclesInParkingLot(String parkingLotName) {
//        List<DetailRegisterVehiclesEntity> detailEntities = this.detailRegisterVehiclesRepository.findByParkingLot_Name(parkingLotName);
//
//        return detailEntities.stream()
//                .map(detail -> detail.getVehicle())
//                .toList();
//    }

    @Override
    public Long save(ParkingLot parkingLot) {
        ParkingLotEntity parkingLotEntity = this.parkingLotMapper.toEntity(parkingLot);
        ParkingLotEntity entity = this.parkingLotRepository.save(parkingLotEntity);
        return entity.getId();
    }
}
