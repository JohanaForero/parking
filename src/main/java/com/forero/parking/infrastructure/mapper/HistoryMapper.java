package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.History;
import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import com.forero.parking.openapi.model.ParkingDepartureResponseDto;
import com.forero.parking.openapi.model.ParkingEntranceResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface HistoryMapper {
    @Mapping(target = "parkingLot.parking.partnerId", ignore = true)
    @Mapping(target = "parkingLot.parking.costPerHour", ignore = true)
    @Mapping(target = "parkingLot.parking.name", ignore = true)
    @Mapping(target = "parkingLot.parking.numberOfParkingLots", ignore = true)
    History toDomain(HistoryEntity historyEntity);

    ParkingEntranceResponseDto toEntranceDto(History history);

    ParkingDepartureResponseDto toDepartureDto(History history);
}
