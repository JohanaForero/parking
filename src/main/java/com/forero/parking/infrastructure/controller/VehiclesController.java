package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingService;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.mapper.VehicleMapper;
import com.forero.parking.infrastructure.util.JwtTokenExtractor;
import com.forero.parking.openapi.api.VehiclesApi;
import com.forero.parking.openapi.model.ParkingVehiclesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class VehiclesController implements VehiclesApi {
    private final ParkingService parkingService;
    private final VehicleMapper vehicleMapper;

    @Override
    public ResponseEntity<ParkingVehiclesResponseDto> getVehiclesInParking(final Integer parkingId) {
        final String token = JwtTokenExtractor.extractTokenFromHeader();
        final List<Vehicle> vehicles = this.parkingService.getVehiclesInParking(token, parkingId);
        return new ResponseEntity<>(this.vehicleMapper.toDto(1, vehicles), HttpStatus.OK);
    }
}
