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

    @Mapping(target = "parkingLot.parking.costPerHour", ignore = true)
    @Mapping(target = "parkingLot.parking.name", ignore = true)
    @Mapping(target = "parkingLot.parking.id", ignore = true)
    @Mapping(target = "parkingLot.entranceDate", ignore = true)
    @Mapping(target = "parkingLot.id", ignore = true)
    @Mapping(target = "departureDate", ignore = true)
    @Mapping(target = "totalPaid", ignore = true)
    @Mapping(target = "entranceDate", ignore = true)
    @Mapping(target = "parkingLot.code", ignore = true)
    @Mapping(target = "vehicle.licensePlate", source = "vehicle.licensePlate")
    @Mapping(target = "vehicle.id", source = "vehicle.id")
    @Mapping(target = "id", source = "id")
    History toEntityToModel(HistoryEntity historyEntity);

//    @Mapping(target = "parkingLot.parking.costPerHour", ignore = true)
//    @Mapping(target = "parkingLot.parking.name", ignore = true)
//    @Mapping(target = "parkingLot.parking.id", ignore = true)
//    @Mapping(target = "parkingLot.entranceDate", ignore = true)
//    @Mapping(target = "parkingLot.id", ignore = true)
//    @Mapping(target = "departureDate", ignore = true)
//    @Mapping(target = "totalPaid", ignore = true)
//    @Mapping(target = "entranceDate", ignore = true)
//    @Mapping(target = "parkingLot.code", source = "parkingLot.code")
//    @Mapping(target = "vehicle.licensePlate", source = "vehicle.licensePlate")
//    @Mapping(target = "vehicle.id", source = "vehicle.id")
//    @Mapping(target = "id", ignore = true)
//    History toDomainToEntity(HistoryEntity historyEntity);
}
