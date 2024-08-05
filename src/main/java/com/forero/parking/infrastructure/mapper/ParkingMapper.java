package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.repository.entity.ParkingEntity;
import com.forero.parking.openapi.model.ParkingRequestDto;
import com.forero.parking.openapi.model.ParkingsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ParkingMapper {
    @Mapping(target = "name", source = "parkingName")
    @Mapping(target = "id", ignore = true)
    Parking toModel(ParkingRequestDto parkingRequestDto);

    List<Parking> toModel(List<ParkingEntity> parkings);

    @Mapping(target = "parkingName", source = "name")
    ParkingEntity toEntity(Parking parking);

    ParkingsResponseDto toDto(Integer dummy, List<Parking> parkings);
}
