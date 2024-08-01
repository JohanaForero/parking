package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingLotService;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.openapi.api.DefaultApi;
import com.forero.parking.openapi.model.ParkingRequestDto;
import com.forero.parking.openapi.model.ParkingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class ParkingController implements DefaultApi {
    private final ParkingLotMapper parkingLotMapper;
    private final ParkingLotService parkingLotService;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<ParkingResponseDto> createParking(ParkingRequestDto parkingRequestDto) {
        final ParkingLot parkingLot = this.parkingLotMapper.toModel(parkingRequestDto);
        final int parkingId = this.parkingLotService.createParking(parkingLot);
        final ParkingResponseDto parkingResponseDto = new ParkingResponseDto();
        parkingResponseDto.setParkingId(parkingId);
        return new ResponseEntity<>(parkingResponseDto, HttpStatus.CREATED);
    }
}
