package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingLotService;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.mapper.ParkingMapper;
import com.forero.parking.openapi.api.DefaultApi;
import com.forero.parking.openapi.model.ParkingRequestDto;
import com.forero.parking.openapi.model.ParkingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class ParkingController implements DefaultApi {
    private static final String LOGGER_PREFIX = String.format("[%s] ", ParkingController.class.getSimpleName());
    private final ParkingMapper parkingMapper;
    private final ParkingLotService parkingLotService;

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParkingResponseDto> createParking(ParkingRequestDto parkingRequestDto) {
        log.info(LOGGER_PREFIX + "[createParking] Request {}", parkingRequestDto);
        final Parking parking = this.parkingMapper.toModel(parkingRequestDto);
        final int parkingId = this.parkingLotService.createParking(parking);
        final ParkingResponseDto parkingResponseDto = new ParkingResponseDto();
        parkingResponseDto.setParkingId(parkingId);
        log.info(LOGGER_PREFIX + "[createParking] Response {}", parkingResponseDto);
        return new ResponseEntity<>(parkingResponseDto, HttpStatus.CREATED);
    }
}
