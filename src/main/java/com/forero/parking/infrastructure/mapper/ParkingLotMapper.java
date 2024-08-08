package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import com.forero.parking.openapi.model.ParkingEntranceRequestDto;
import com.forero.parking.openapi.model.ParkingVehiclesResponseDto;
import com.forero.parking.openapi.model.VehicleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper
public interface ParkingLotMapper {
    @Mapping(target = "parking.id", source = "parkingId")
    @Mapping(target = "vehicle.licensePlate", source = "licensePlate")
    @Mapping(target = "entranceDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    ParkingLot toDomain(ParkingEntranceRequestDto parkingEntranceRequestDto);

    @Mapping(target = "id", source = "parkingLotId")
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "entranceDate", ignore = true)
    @Mapping(target = "parking", ignore = true)
    ParkingLot toDomain(Long parkingLotId);

    @Mapping(target = "parking.name", source = "parking.parkingName")
    ParkingLot toDomain(ParkingLotEntity parkingLotEntity);

    default ParkingVehiclesResponseDto toDto(final List<ParkingLot> parkingLots) {
        if (parkingLots == null) {
            return null;
        }

        final ParkingVehiclesResponseDto parkingVehiclesResponseDto = new ParkingVehiclesResponseDto();
        parkingVehiclesResponseDto.vehicles(this.toDtos(parkingLots));

        return parkingVehiclesResponseDto;
    }

    List<VehicleDto> toDtos(List<ParkingLot> parkingLots);

    @Mapping(target = "id", source = "vehicle.id")
    @Mapping(target = "licensePlate", source = "vehicle.licensePlate")
    @Mapping(target = "parkingLotId", source = "id")
    VehicleDto toDto(ParkingLot parkingLot);

    default OffsetDateTime map(final LocalDateTime localDateTime) {
        return localDateTime != null ? OffsetDateTime.of(localDateTime, OffsetDateTime.now().getOffset()) : null;
    }

}
