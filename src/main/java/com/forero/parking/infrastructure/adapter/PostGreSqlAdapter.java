package com.forero.parking.infrastructure.adapter;

import com.forero.parking.application.configuration.TimeConfiguration;
import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.exception.EntranceException;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.infrastructure.mapper.ParkingMapper;
import com.forero.parking.infrastructure.repository.HistoryRepository;
import com.forero.parking.infrastructure.repository.ParkingLotRepository;
import com.forero.parking.infrastructure.repository.ParkingRepository;
import com.forero.parking.infrastructure.repository.VehicleRepository;
import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import com.forero.parking.infrastructure.repository.entity.ParkingEntity;
import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostGreSqlAdapter implements DbPort {
    private static final String LOGGER_PREFIX = String.format("[%s] ", PostGreSqlAdapter.class.getSimpleName());
    private final VehicleRepository vehicleRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingRepository parkingRepository;
    private final HistoryRepository historyRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final ParkingMapper parkingMapper;
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
        final ParkingEntity parkingEntity = new ParkingEntity();
        parkingEntity.setId(parkingLot.getParking().getId());

        final LocalDateTime entranceDate = this.timeConfiguration.now();

        parkingLotEntity.setVehicle(vehicleEntity);
        parkingLotEntity.setEntranceDate(entranceDate);
        parkingLotEntity.setParking(parkingEntity);
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
    public int saveParking(final Parking parking) {
        final ParkingEntity parkingEntity = this.parkingMapper.toEntity(parking);
        final ParkingEntity entity = this.parkingRepository.save(parkingEntity);
        return entity.getId().intValue();
    }

    @Override
    public boolean existsParkingName(final String parkingName) {
        log.info(LOGGER_PREFIX + "[existsParkingName] Request {}", parkingName);
        final boolean result = this.parkingRepository.existsByParkingName(parkingName);
        log.info(LOGGER_PREFIX + "[existsParkingName] Response {}", result);
        return !result;
    }

    @Override
    public List<Parking> findAllParking(final String partnerId) {
        final List<ParkingEntity> parkingEntities = this.parkingRepository.findByPartnerId(partnerId);
        log.info(LOGGER_PREFIX + "[findAllParking] Response {}", parkingEntities);
        return this.parkingMapper.toModel(parkingEntities);
    }

    @Override
    public int getNumberOfParkingLots(final int parkingId) {
        log.info(LOGGER_PREFIX, "[getNumberOfParkingLots] Request {}", parkingId);
        final ParkingEntity parkingEntity = this.parkingRepository.findById((long) parkingId)
                .orElseThrow(() -> new EntranceException.NotFoundParkingException("Parking lot not found with id: " + parkingId));
        return parkingEntity.getNumberOfParkingLots();
    }

    @Override
    public boolean existsParkingByPartnerId(final int parkingId, @NonNull final String partnerId) {
        log.info(LOGGER_PREFIX, "[existsParkingByPartnerId] Request {} {}", parkingId, partnerId);
        final boolean result = this.parkingRepository.existsByIdAndPartnerId(parkingId, partnerId);
        log.info(LOGGER_PREFIX + "[existsParkingByPartnerId] Response {}", result);
        return !result;
    }

    @Override
    public boolean thereIsAPlaqueInTheParking(@NonNull final String licensePlate, final Long parkingId) {
        log.info(LOGGER_PREFIX, "[thereIsAPlaqueInTheParking] Request {} {}", licensePlate, parkingId);
        final boolean result = this.parkingLotRepository.existsByParkingIdAndVehicleLicensePlate(licensePlate, parkingId);
        log.info(LOGGER_PREFIX + "[thereIsAPlaqueInTheParking] Response {}", result);
        return !result;
    }
}
