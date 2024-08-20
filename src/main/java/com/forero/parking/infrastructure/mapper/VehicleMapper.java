package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.agregate.Pagination;
import com.forero.parking.domain.agregate.VehicleAgregate;
import com.forero.parking.domain.agregate.VehiclePageResult;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
import com.forero.parking.openapi.model.FirstTimeVehicleResponseDto;
import com.forero.parking.openapi.model.FirstTimeVehicleResponseFirstTimeVehiclesInnerDto;
import com.forero.parking.openapi.model.PaginationRequestDto;
import com.forero.parking.openapi.model.VehicleParkingDto;
import com.forero.parking.openapi.model.VehicleParkingResponseDto;
import com.forero.parking.openapi.model.VehicleResponseDto;
import com.forero.parking.openapi.model.VehiclesResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper
public interface VehicleMapper {
    Vehicle toDomain(VehicleEntity vehicleEntity);

    @Mapping(target = "licensePlate", source = "vehicle.licensePlate")
    @Mapping(target = "id", source = "vehicle.id")
    @Mapping(target = "code", source = "parkingLot.code")
    VehicleResponseDto toModelToDto(History vehicles);

    default OffsetDateTime map(final LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }

    @Mapping(target = "pageSize", source = "pageSize")
    @Mapping(target = "page", source = "page")
    @Mapping(target = "totalPages", ignore = true)
    @Mapping(target = "total", ignore = true)
    Pagination toModelToPagination(PaginationRequestDto paginationRequestDto);

    @Mapping(target = "vehicles", source = "vehicles")
    @Mapping(target = "paginationResponse.total", source = "pagination.total")
    @Mapping(target = "paginationResponse.totalPages", source = "pagination.totalPages")
    VehiclesResponseDto toDto(VehiclePageResult<History> vehicleVehiclePageResult);

    @Mapping(target = "licensePlate", source = "vehicle.licensePlate")
    @Mapping(target = "total", source = "parkingLot.code")
    @Mapping(target = "id", source = "vehicle.id")
    VehicleParkingDto toDto(History history);

    List<VehicleParkingDto> toDtoList(List<History> historyList);


    @Mapping(target = "vehicleParking", source = "vehicles")
    VehicleParkingResponseDto toDomainToDto(Integer dummy, List<History> vehicles);

    FirstTimeVehicleResponseFirstTimeVehiclesInnerDto toFirstDto(VehicleAgregate vehicle);

    FirstTimeVehicleResponseDto toModelVehiclesToDto(Integer dummy, List<VehicleAgregate> firstTimeVehicles);
}