package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingLotService;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.openapi.api.VehiclesApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class VehiclesController implements VehiclesApi {
    private final ParkingLotService parkingLotService;
    private final ParkingLotMapper parkingLotMapper;

//    @Override
//    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
//    public ResponseEntity<ParkingVehiclesResponseDto> getVehiclesInParking() {
//
//        final List<Vehicle> parkingLots = this.parkingLotService.getVehiclesInParking();
//
//        return new ResponseEntity<>(this.parkingLotMapper.toDto(parkingLots), HttpStatus.OK);
//    }
}
