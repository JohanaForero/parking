package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingLotService;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.openapi.api.DepartureApi;
import com.forero.parking.openapi.model.ParkingDepartureResponseDto;
import com.forero.parking.openapi.model.ParkingEntranceRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class DepartureController implements DepartureApi {
    private final ParkingLotService parkingLotService;
    private final ParkingLotMapper parkingLotMapper;
    private final HistoryMapper historyMapper;

    @Override
    @PreAuthorize("hasAuthority('PARTNER')")
    public ResponseEntity<ParkingDepartureResponseDto> registerVehicleExit(final ParkingEntranceRequestDto parkingDepartureRequestDto) {
        final ParkingLot parkingLot = this.parkingLotMapper.toDomain(parkingDepartureRequestDto);

        final History history = this.parkingLotService.registerVehicleExit(parkingLot);

        return new ResponseEntity<>(this.historyMapper.toDepartureDto(history), HttpStatus.OK);
    }
}
