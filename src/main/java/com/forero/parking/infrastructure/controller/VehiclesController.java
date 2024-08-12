package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingService;
import com.forero.parking.domain.agregate.Pagination;
import com.forero.parking.domain.agregate.VehiclePageResult;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.mapper.ParkingMapper;
import com.forero.parking.infrastructure.mapper.VehicleMapper;
import com.forero.parking.infrastructure.util.JwtTokenExtractor;
import com.forero.parking.openapi.api.VehiclesApi;
import com.forero.parking.openapi.model.VehiclesRequestDto;
import com.forero.parking.openapi.model.VehiclesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class VehiclesController implements VehiclesApi {
    private final ParkingService parkingService;
    private final VehicleMapper vehicleMapper;
    private final ParkingMapper parkingMapper;

//    @Override
//    public ResponseEntity<ParkingVehiclesResponseDto> getVehiclesInParking(final Integer parkingId) {
//        final String token = JwtTokenExtractor.extractTokenFromHeader();
//        final List<Vehicle> vehicles = this.parkingService.getVehiclesInParking(token, parkingId);
//        return new ResponseEntity<>(this.vehicleMapper.toDto(1, vehicles), HttpStatus.OK);
//    }


    @Override
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
}
