package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import com.forero.parking.openapi.model.ParkingRequestDto;
import com.forero.parking.openapi.model.VehicleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper
public interface ParkingLotMapper {
    ParkingLot toDomain(ParkingLotEntity parkingLotEntity);

    ParkingLot toModel(ParkingLotEntity parkingLotId);

    @Mapping(target = "id", source = "vehicle.id")
    @Mapping(target = "licensePlate", source = "vehicle.licensePlate")
    @Mapping(target = "parkingLotId", source = "id")
    VehicleDto toDto(ParkingLot parkingLot);

    default OffsetDateTime map(final LocalDateTime localDateTime) {
        return localDateTime != null ? OffsetDateTime.of(localDateTime, OffsetDateTime.now().getOffset()) : null;
    }

    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "entranceDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    ParkingLot toModel(ParkingRequestDto parkingRequestDto);

    ParkingLotEntity toEntity(ParkingLot parkingLot);
}
