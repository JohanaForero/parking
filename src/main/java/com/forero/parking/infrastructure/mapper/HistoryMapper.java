package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.History;
import com.forero.parking.infrastructure.repository.entity.HistoryEntity;
import com.forero.parking.openapi.model.ParkingDepartureResponseDto;
import com.forero.parking.openapi.model.ParkingEntranceResponseDto;
import org.mapstruct.Mapper;

@Mapper
public interface HistoryMapper {
    History toDomain(HistoryEntity historyEntity);

    ParkingEntranceResponseDto toEntranceDto(History history);

    ParkingDepartureResponseDto toDepartureDto(History history);
}
