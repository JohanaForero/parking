package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.repository.entity.ParkingEntity;
import com.forero.parking.openapi.model.ParkingRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ParkingMapper {
    @Mapping(target = "name", source = "parkingName")
    @Mapping(target = "id", ignore = true)
    Parking toModel(ParkingRequestDto parkingRequestDto);

    @Mapping(target = "parkingName", source = "name")
    ParkingEntity toEntity(Parking parking);
}
