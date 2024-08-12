package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingService;
import com.forero.parking.domain.model.Parking;
import com.forero.parking.infrastructure.mapper.ParkingMapper;
import com.forero.parking.infrastructure.util.JwtTokenExtractor;
import com.forero.parking.openapi.api.CentralParkingApi;
import com.forero.parking.openapi.model.ParkingRequestDto;
import com.forero.parking.openapi.model.ParkingResponseDto;
import com.forero.parking.openapi.model.ParkingsResponseDto;
import com.forero.parking.openapi.model.UpdateParkingRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<ParkingsResponseDto> getParkings() {
        final String token = JwtTokenExtractor.extractTokenFromHeader();
        final List<Parking> parkingList = this.parkingService.getParkings(token);
        final ParkingsResponseDto parkingsResponseDto = this.parkingMapper.toDto(1, parkingList);
        return ResponseEntity.ok(parkingsResponseDto);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<ParkingRequestDto> getParkingById(final Integer idParking) {
        final String token = JwtTokenExtractor.extractTokenFromHeader();
        final Parking parkingResponseDomain = this.parkingService.getParking(idParking, token);
        final ParkingRequestDto parkingResponseDto = this.parkingMapper.toDto(parkingResponseDomain);
        return new ResponseEntity<>(parkingResponseDto, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteParkingById(final Integer idParking) {
        this.parkingService.deleteParking(idParking);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateParkingPartially(final UpdateParkingRequestDto updateParkingRequestDto) {
        final Parking parking = this.parkingMapper.toDtoToModel(updateParkingRequestDto);
        this.parkingService.updatePartially(parking);
        return ResponseEntity.noContent().build();
    }
}
