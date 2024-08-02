package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingService;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.mapper.ParkingMapper;
import com.forero.parking.infrastructure.util.JwtTokenExtractor;
import com.forero.parking.infrastructure.util.JwtUtil;
import com.forero.parking.openapi.api.CentralParkingApi;
import com.forero.parking.openapi.model.ParkingRequestDto;
import com.forero.parking.openapi.model.ParkingResponseDto;
import com.forero.parking.openapi.model.ParkingsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class ParkingController implements CentralParkingApi {
    private static final String LOGGER_PREFIX = String.format("[%s] ", ParkingController.class.getSimpleName());
    private final ParkingMapper parkingMapper;
    private final ParkingService parkingService;

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ParkingResponseDto> createParking(final ParkingRequestDto parkingRequestDto) {
        log.info(LOGGER_PREFIX + "[createParking] Request {}", parkingRequestDto);
        final Parking parking = this.parkingMapper.toModel(parkingRequestDto);
        final int parkingId = this.parkingService.createParking(parking);
        final ParkingResponseDto parkingResponseDto = new ParkingResponseDto();
        parkingResponseDto.setParkingId(parkingId);
        log.info(LOGGER_PREFIX + "[createParking] Response {}", parkingResponseDto);
        return new ResponseEntity<>(parkingResponseDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ParkingsResponseDto> getParkings() {
        final String token = JwtTokenExtractor.extractTokenFromHeader();
        final String partnerId = JwtUtil.getClaimFromToken(token, JwtClaimNames.SUB);
        return CentralParkingApi.super.getParkings();
    }
}
