package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.agregate.Pagination;
import com.forero.parking.domain.agregate.VehiclePageResult;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
import com.forero.parking.openapi.model.PaginationRequestDto;
import com.forero.parking.openapi.model.VehicleResponseDto;
import com.forero.parking.openapi.model.VehiclesResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper
public interface VehicleMapper {
    @Mapping(target = "id", ignore = true)
    Vehicle toDomain(String licensePlate);

    Vehicle toDomain(VehicleEntity vehicleEntity);

    @Mapping(target = "licensePlate", source = "vehicle.licensePlate")
    @Mapping(target = "id", source = "vehicle.id")
    @Mapping(target = "code", source = "parkingLot.code")
    VehicleResponseDto toModelToDto(History vehicles);

    default OffsetDateTime map(final LocalDateTime localDateTime) {
        return localDateTime != null ? OffsetDateTime.of(localDateTime, OffsetDateTime.now().getOffset()) : null;
    }

    @Mapping(target = "pageSize", source = "pageSize")
    @Mapping(target = "page", source = "page")
    Pagination toModelToPagination(PaginationRequestDto paginationRequestDto);

    @Mapping(target = "vehicles", source = "vehicles")
    @Mapping(target = "paginationResponse.total", source = "pagination.total")
    @Mapping(target = "paginationResponse.totalPages", source = "pagination.totalPages")
    VehiclesResponseDto toDto(VehiclePageResult<History> vehicleVehiclePageResult);
}