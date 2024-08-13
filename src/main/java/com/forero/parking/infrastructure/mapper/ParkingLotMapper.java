package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import com.forero.parking.openapi.model.ParkingEntranceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ParkingLotMapper {
    @Mapping(target = "parking.id", source = "parkingId")
    @Mapping(target = "entranceDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    ParkingLot toDomain(ParkingEntranceRequestDto parkingEntranceRequestDto);

    @Mapping(target = "id", source = "parkingLotId")
    @Mapping(target = "entranceDate", ignore = true)
    @Mapping(target = "parking", ignore = true)
    @Mapping(target = "code", ignore = true)
    ParkingLot toDomain(Long parkingLotId);

    @Mapping(target = "parking.name", source = "parking.parkingName")
    ParkingLot toDomain(ParkingLotEntity parkingLotEntity);
}
