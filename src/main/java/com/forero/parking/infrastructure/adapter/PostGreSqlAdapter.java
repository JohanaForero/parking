package com.forero.parking.infrastructure.adapter;

import com.forero.parking.application.configuration.TimeConfiguration;
import com.forero.parking.application.port.DbPort;
import com.forero.parking.domain.agregate.Pagination;
import com.forero.parking.domain.agregate.VehicleAgregate;
import com.forero.parking.domain.exception.DepartureException;
import com.forero.parking.domain.exception.EntranceException;
import com.forero.parking.domain.exception.ParkingException;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.infrastructure.mapper.ParkingMapper;
import com.forero.parking.infrastructure.mapper.VehicleMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostGreSqlAdapter implements DbPort {
    private static final String LOGGER_PREFIX = String.format("[%s] ", PostGreSqlAdapter.class.getSimpleName());
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingRepository parkingRepository;
    private final HistoryRepository historyRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final ParkingMapper parkingMapper;
    private final HistoryMapper historyMapper;
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;
    private final TimeConfiguration timeConfiguration;

    @Override
    public ParkingLot registerVehicleEntry(final ParkingLot parkingLot, final String licensePlate) {
        this.vehicleRepository.findByLicensePlate(licensePlate)
                .orElseGet(() -> {
                    final VehicleEntity newVehicleEntity = new VehicleEntity();
                    newVehicleEntity.setLicensePlate(licensePlate);
                    return this.vehicleRepository.save(newVehicleEntity);
                });

        final ParkingEntity parking = this.parkingRepository.findById(parkingLot.getParkingId())
                .orElse(null);
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity();
        final LocalDateTime entranceDate = this.timeConfiguration.now();
        parkingLotEntity.setParking(parking);
        parkingLotEntity.setEntranceDate(entranceDate);
        parkingLotEntity.setCode(parkingLot.getCode());
        parkingLotEntity = this.parkingLotRepository.save(parkingLotEntity);

        return this.parkingLotMapper.toDomain(parkingLotEntity);
    }

    @Override
    public History registerHistoryEntry(final ParkingLot parkingLot, final String licensePlate) {
        final VehicleEntity vehicleEntity = this.vehicleRepository.findByLicensePlate(licensePlate)
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
    public Vehicle getVehicle(final String licensePlate) {
        final VehicleEntity vehicleEntity = this.vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new DepartureException.VehicleNoFound("Vehicle no found: " + licensePlate));
        return this.vehicleMapper.toDomain(vehicleEntity);
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
                .orElseThrow(() -> new EntranceException.NotFoundParkingException("Parking not found with id: " + parkingId));
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
    public Parking findById(final long parkingId) {
        final ParkingEntity parkingEntity =
                this.parkingRepository.findById(parkingId).orElseThrow(() -> new ParkingException
                        .ParkingNoFoundException("Parking not found: " + parkingId));
        return this.parkingMapper.toModel(parkingEntity);
    }

    @Override
    public boolean existsVehicleInParking(final int parkingId, final String licensePlate) {
        log.info(LOGGER_PREFIX, "[existsVehicleInParking] Request {} {} {}", parkingId, licensePlate);
        final boolean result = this.historyRepository.existsByParkingLotParkingIdAndVehicleLicensePlateAndDepartureDateIsNullAndTotalPaidIsNull(parkingId, licensePlate);
        log.info(LOGGER_PREFIX + "[existsParkingByPartnerId] Response {}", result);
        return result;
    }

    @Override
    public boolean thereIsAVehicleInTheParkingLot(final ParkingLot parkingLot, final String licensePlate) {
        log.info(LOGGER_PREFIX + "[ThereIsAVehicleInTheParkingLot] Request {} {} {}", parkingLot.getParkingId(),
                parkingLot.getCode(), licensePlate);
        final boolean result =
                this.historyRepository.existsByParkingLotParkingIdAndParkingLotCodeAndVehicleLicensePlateAndDepartureDateIsNullAndTotalPaidIsNull(parkingLot.getParkingId().intValue(),
                        parkingLot.getCode(), licensePlate);
        log.info(LOGGER_PREFIX + "[ThereIsAVehicleInTheParkingLot] Response {}", result);
        return result;
    }

    @Override
    public ParkingLot getParkingLotByCodeAndParkingEntry(final int code, final int parkingId, final String licensePlate) {
        final HistoryEntity history =
                this.historyRepository.findByParkingLotParkingIdAndParkingLotCodeAndVehicleLicensePlateAndDepartureDateIsNullAndTotalPaidIsNull(
                        parkingId, code, licensePlate).orElseThrow(() -> new DepartureException.VehicleNoFound("The car is not in that code"));
        final ParkingLotEntity parkingLotEntity =
                this.parkingLotRepository.findById(history.getParkingLot().getId()).orElse(null);
        return this.parkingLotMapper.toDomain(parkingLotEntity);
    }

    @Override
    public boolean codeIsFree(final int parkingId, final int code) {
        final boolean result =
                this.historyRepository.existsByParkingLotParkingIdAndParkingLotCodeAndDepartureDateIsNullAndTotalPaidIsNull(parkingId, code);
        log.info(LOGGER_PREFIX + "[codeIsFree] Response {}", result);
        return result;
    }

    @Override
    public List<Parking> getAllParkings() {
        final List<ParkingEntity> parkings = this.parkingRepository.findAll();
        return this.parkingMapper.toModel(parkings);
    }

    @Override
    public void deleteParking(final int parkingId) {
        final ParkingEntity parkingEntity =
                this.parkingRepository.findById((long) parkingId).orElseThrow(() -> new ParkingException
                        .ParkingNoFoundException("Parking not found: " + parkingId));
        final List<ParkingLotEntity> parkingLots = this.parkingLotRepository.findByParkingId(parkingEntity.getId());
        for (final ParkingLotEntity parkingLot : parkingLots) {
            final List<HistoryEntity> historyEntries = this.historyRepository.findByParkingLotId(parkingLot.getId());

            this.historyRepository.deleteAll(historyEntries);

            this.parkingLotRepository.delete(parkingLot);
        }
        this.parkingRepository.delete(parkingEntity);
    }

    @Override
    public void updatePartially(final Parking parking) {
        final long parkingId = parking.getId();
        final ParkingEntity parkingEntity =
                this.parkingRepository.findById(parkingId).orElseThrow(() -> new ParkingException
                        .ParkingNoFoundException("Parking not found: " + parkingId));
        parkingEntity.setCostPerHour(parking.getCostPerHour());
        this.parkingRepository.save(parkingEntity);
    }

    @Override
    public void updateParking(final Parking parking) {
        final long parkingId = parking.getId();
        final ParkingEntity parkingEntity =
                this.parkingRepository.findById(parkingId).orElseThrow(() -> new ParkingException
                        .ParkingNoFoundException("Parking not found: " + parkingId));
        parkingEntity.setParkingName(parking.getName());
        parkingEntity.setPartnerId(parking.getPartnerId());
        parkingEntity.setNumberOfParkingLots(parking.getNumberOfParkingLots());
        parkingEntity.setCostPerHour(parking.getCostPerHour());
        this.parkingRepository.save(parkingEntity);
    }

    @Override
    public boolean getCurrentParkingName(final Parking parking) {
        return this.parkingRepository.existsByIdAndParkingName(parking.getId(), parking.getName());
    }

    @Override
    public int getTotalVehicles(final int parkingId) {
        final Long result = this.historyRepository.countVehiclesInParking(parkingId);
        return result.intValue();
    }

    @Override
    public List<History> getVehicles(final int parkingId, @NonNull final Pagination pagination) {
        final int page = pagination.getPage();
        final int pageSize = pagination.getPageSize();
        final Pageable pageable = PageRequest.of(page, pageSize);
        final Page<HistoryEntity> historyEntities = this.historyRepository.findActiveHistoriesByParkingId(parkingId, pageable);
        log.info(LOGGER_PREFIX + "[getVehiclesAdmin] Response {}", historyEntities);
        return historyEntities.stream()
                .map(this.historyMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsVehiclesInParking(final int parkingId) {
        final long totalVehicles = this.historyRepository.countVehiclesInParking(parkingId);
        return totalVehicles > 0;
    }

    @Override
    public boolean isCodeIsLowerThanCurrent(final int parkingId, final int numberOfParkingLots) {
        final boolean isCodeIsLowerThanCurrent =
                this.historyRepository.existsActiveParkingLotWithHigherCode(parkingId, numberOfParkingLots);
        log.info(LOGGER_PREFIX + "[isCodeIsLowerThanCurrent] Response {}", isCodeIsLowerThanCurrent);
        return isCodeIsLowerThanCurrent;
    }

    @Override
    public List<History> getVehiclesStatics(final int parkingId) {
        final List<History> statics23 = new ArrayList<>();
        final Pageable topTen = PageRequest.of(0, 10);
        final List<VehicleEntity> vehicles = this.historyRepository.findTop10VehiclesByEntriesAndDurationInParking(parkingId, topTen);
        for (final VehicleEntity vehicle : vehicles) {
            final int entryCount = this.historyRepository.countEntriesByVehicleInParking(parkingId, vehicle.getId());
            final Vehicle vehicle2 = this.vehicleMapper.toDomain(vehicle);
            final ParkingLot parkingLot = new ParkingLot();
            parkingLot.setCode(entryCount);
            final History history = new History();
            history.setVehicle(vehicle2);
            history.setParkingLot(parkingLot);
            statics23.add(history);
        }
        return statics23;
    }

    @Override
    public int findIdByName(String parkingName) {
        final Long parkingId = this.parkingRepository.findIdByParkingName(parkingName);
        return parkingId.intValue();
    }

    @Override
    public boolean vehicleExistsInTheParkingAtTheMoment(final int parkingId, final String licensePlate) {
        log.info(LOGGER_PREFIX + "[vehicleExistsInTheParkingAtTheMoment] Request {}", parkingId);
        return this.historyRepository.isVehicleInParking(licensePlate, (long) parkingId);
    }

    @Override
    public List<History> getTopRegisteredVehicles() {
        final List<History> statics = new ArrayList<>();
        Page<Object[]> topVehicles = historyRepository.findTop10VehiclesByTotalEntries(PageRequest.of(0, 10));
        for (Object[] result : topVehicles.getContent()) {
            VehicleEntity vehicleEntity = (VehicleEntity) result[0];
            final Vehicle vehicle = this.vehicleMapper.toDomain(vehicleEntity);
            int visitCount = ((Long) result[1]).intValue();
            final ParkingLot parkingLot = new ParkingLot();
            parkingLot.setCode(visitCount);
            final History history = new History();
            history.setVehicle(vehicle);
            history.setParkingLot(parkingLot);
            statics.add(history);
        }
        return statics;
    }

    @Override
    public List<VehicleAgregate> getVehiclesParkedForTheFirstTime(int parkingId) {
        final List<VehicleAgregate> result = new ArrayList<>();
        final List<VehicleEntity> vehicleEntities =
                this.historyRepository.findVehiclesCurrentlyParkedInParking(parkingId);
        for (final VehicleEntity vehicle : vehicleEntities) {
            VehicleAgregate vehicleAgregate = new VehicleAgregate();
            vehicleAgregate.setVehicleId(vehicle.getId());
            vehicleAgregate.setLicensePlate(vehicle.getLicensePlate());
            final int timesParked = this.historyRepository.countVehicleEntriesInParking(vehicle.getId(), (long) parkingId);
            boolean isFirstTime = timesParked == 1;
            vehicleAgregate.setIsFirstTime(isFirstTime);
            result.add(vehicleAgregate);

        }
        return result;
    }
}
