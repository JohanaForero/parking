package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface VehicleMapper {
    @Mapping(target = "id", ignore = true)
    Vehicle toDomain(String licensePlate);

    Vehicle toDomain(VehicleEntity vehicleEntity);
}
