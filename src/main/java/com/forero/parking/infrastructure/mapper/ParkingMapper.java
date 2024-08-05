package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.repository.entity.ParkingEntity;
import com.forero.parking.openapi.model.ParkingDto;
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

    @Mapping(target = "name", source = "parkingName")
    @Mapping(target = "partnerId", ignore = true)
    @Mapping(target = "costPerHour", ignore = true)
    @Mapping(target = "numberOfParkingLots", ignore = true)
    Parking toModel(ParkingEntity parkingEntity);

    @Mapping(source = "id", target = "parkingId")
    @Mapping(source = "name", target = "parkingName")
    ParkingDto parkingToParkingDto(Parking parking);

    List<ParkingDto> parkingListToParkingDtoList(List<Parking> parkings);

    @Mapping(target = "parking", source = "parkings")
    ParkingsResponseDto toDto(Integer dummy, List<Parking> parkings);
}
