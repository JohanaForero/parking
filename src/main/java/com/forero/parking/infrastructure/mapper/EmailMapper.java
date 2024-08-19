package com.forero.parking.infrastructure.mapper;

import com.forero.parking.infrastructure.adapter.dto.EmailRequestDto;
import com.forero.parking.infrastructure.adapter.gateways.Email;
import com.forero.parking.openapi.model.ParkingEmailRequestDto;
import com.forero.parking.openapi.model.ParkingEmailResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EmailMapper {
    @Mapping(target = "address", source = "email")
    Email toDomain(ParkingEmailRequestDto parkingEmailRequestDto);

    @Mapping(target = "message", source = "response")
    ParkingEmailResponseDto toDto(String response);

    @Mapping(target = "email", source = "address")
    @Mapping(target = "placa", source = "licensePlate")
    @Mapping(target = "mensaje", source = "message")
    @Mapping(target = "parqueaderoNombre", source = "parkingName")
    EmailRequestDto toDto(Email email);
}
