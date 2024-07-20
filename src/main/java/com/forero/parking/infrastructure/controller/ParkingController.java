package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingLotService;
import com.forero.parking.openapi.api.EntranceApi;
import com.forero.parking.openapi.model.ParkingEntranceRequestDto;
import com.forero.parking.openapi.model.ParkingEntranceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class ParkingController implements EntranceApi {
    private final ParkingLotService parkingLotService;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<ParkingEntranceResponseDto> registerVehicleEntry(final ParkingEntranceRequestDto parkingEntranceRequestDto) {
        final ParkingEntranceResponseDto parkingEntranceResponseDto = new ParkingEntranceResponseDto();
        parkingEntranceResponseDto.setId(this.parkingLotService.registerVehicleEntry());
        return new ResponseEntity<>(parkingEntranceResponseDto, HttpStatus.CREATED);
    }
}
