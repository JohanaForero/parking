package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingService;
import com.forero.parking.domain.agregate.Pagination;
import com.forero.parking.domain.agregate.VehicleAgregate;
import com.forero.parking.domain.agregate.VehiclePageResult;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.mapper.ParkingMapper;
import com.forero.parking.infrastructure.mapper.VehicleMapper;
import com.forero.parking.infrastructure.util.JwtTokenExtractor;
import com.forero.parking.openapi.api.VehiclesApi;
import com.forero.parking.openapi.model.FirstTimeVehicleResponseDto;
import com.forero.parking.openapi.model.VehicleParkingResponseDto;
import com.forero.parking.openapi.model.VehiclesRequestDto;
import com.forero.parking.openapi.model.VehiclesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class VehiclesController implements VehiclesApi {
    private final ParkingService parkingService;
    private final VehicleMapper vehicleMapper;
    private final ParkingMapper parkingMapper;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<VehiclesResponseDto> getVehiclesInParking(final VehiclesRequestDto vehiclesRequestDto) {
        final Parking parking = this.parkingMapper.toDomain(vehiclesRequestDto.getParkingId().longValue());
        final Pagination paginationRequest =
                this.vehicleMapper.toModelToPagination(vehiclesRequestDto.getPaginationRequest());
        final String token = JwtTokenExtractor.extractTokenFromHeader();
        final VehiclePageResult<History> vehicleVehiclePageResult = this.parkingService.getVehiclesInParking(token, parking,
                paginationRequest);
        final VehiclesResponseDto vehiclesResponseDto = this.vehicleMapper.toDto(vehicleVehiclePageResult);
        return new ResponseEntity<>(vehiclesResponseDto, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<VehicleParkingResponseDto> getLimitedVehiclesInParkingById(final Long parkingId) {
        final List<History> vehicles = this.parkingService.getLimitedVehiclesInParkingById(parkingId.intValue());
        final VehicleParkingResponseDto dtoResponse = this.vehicleMapper.toDomainToDto(1, vehicles);
        return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VehicleParkingResponseDto> getTopRegisteredVehicles() {
        final List<History> topVehicles = this.parkingService.getTopRegisteredVehicles();
        final VehicleParkingResponseDto dtoResponse = this.vehicleMapper.toDomainToDto(1, topVehicles);
        return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<FirstTimeVehicleResponseDto> verifyFirstTimeParkedVehicles(final Long parkingId) {
        final List<VehicleAgregate> vehicleAgregates =
                this.parkingService.getVehiclesParkedForTheFirstTime(parkingId.intValue());
        final FirstTimeVehicleResponseDto vehiclesDto = this.vehicleMapper.toModelVehiclesToDto(1, vehicleAgregates);
        return new ResponseEntity<>(vehiclesDto, HttpStatus.OK);
    }
}
