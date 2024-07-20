package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.infrastructure.repository.entity.ParkingLotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ParkingLotMapper {
    @Mapping(target = "id", source = "parkingLotId")
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "entranceDate", ignore = true)
    ParkingLot toDomain(Long parkingLotId);

    ParkingLot toDomain(ParkingLotEntity parkingLotEntity);
}
