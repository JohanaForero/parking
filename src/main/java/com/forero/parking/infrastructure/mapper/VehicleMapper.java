package com.forero.parking.infrastructure.mapper;

import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.repository.entity.VehicleEntity;
import com.forero.parking.openapi.model.ParkingVehiclesResponseDto;
import com.forero.parking.openapi.model.VehicleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface VehicleMapper {
    @Mapping(target = "id", ignore = true)
    Vehicle toDomain(String licensePlate);

    Vehicle toDomain(VehicleEntity vehicleEntity);


    @Mapping(target = "vehicles", source = "vehicles")
    ParkingVehiclesResponseDto toDto(Integer dummy, List<Vehicle> vehicles);

    VehicleDto toModelToDto(Vehicle vehicle);
}