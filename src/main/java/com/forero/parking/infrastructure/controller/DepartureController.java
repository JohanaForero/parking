package com.forero.parking.infrastructure.controller;

import com.forero.parking.openapi.api.DepartureApi;
import com.forero.parking.openapi.model.ParkingDepartureRequestDto;
import com.forero.parking.openapi.model.ParkingDepartureResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class DepartureController implements DepartureApi {

    @Override
    public ResponseEntity<ParkingDepartureResponseDto> registerVehicleExit(ParkingDepartureRequestDto parkingDepartureRequestDto) {
        return DepartureApi.super.registerVehicleExit(parkingDepartureRequestDto);
    }
}
