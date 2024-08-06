package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingLotService;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.infrastructure.mapper.VehicleMapper;
import com.forero.parking.infrastructure.util.JwtTokenExtractor;
import com.forero.parking.infrastructure.util.JwtUtil;
import com.forero.parking.openapi.api.EntranceApi;
import com.forero.parking.openapi.model.ParkingEntranceRequestDto;
import com.forero.parking.openapi.model.ParkingEntranceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class EntranceController implements EntranceApi {
    private final ParkingLotService parkingLotService;
    private final ParkingLotMapper parkingLotMapper;
    private final VehicleMapper vehicleMapper;
    private final HistoryMapper historyMapper;

    @Override
    @PreAuthorize("hasAuthority('PARTNER')")
    public ResponseEntity<ParkingEntranceResponseDto> registerVehicleEntry(final ParkingEntranceRequestDto parkingEntranceRequestDto) {
        final ParkingLot parkingLot = this.parkingLotMapper.toDomain(parkingEntranceRequestDto);
        final Vehicle vehicle = this.vehicleMapper.toDomain(parkingEntranceRequestDto.getLicensePlate());
        final String token = JwtTokenExtractor.extractTokenFromHeader();
        final String partnerId = JwtUtil.getClaimFromToken(token, JwtClaimNames.SUB);
        final History history = this.parkingLotService.registerVehicleEntry(parkingLot, vehicle, partnerId);

        return new ResponseEntity<>(this.historyMapper.toEntranceDto(history), HttpStatus.CREATED);
    }
}
