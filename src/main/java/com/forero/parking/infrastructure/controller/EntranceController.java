package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingLotService;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
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
public class EntranceController implements EntranceApi {
    private final ParkingLotService parkingLotService;
    private final ParkingLotMapper parkingLotMapper;
    private final HistoryMapper historyMapper;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<ParkingEntranceResponseDto> registerVehicleEntry(final ParkingEntranceRequestDto parkingEntranceRequestDto) {
        final ParkingLot parkingLot = this.parkingLotMapper.toDtoForModel(parkingEntranceRequestDto);
        final History history = this.parkingLotService.registerVehicleEntry(parkingLot);

        return new ResponseEntity<>(this.historyMapper.toEntranceDto(history), HttpStatus.CREATED);
    }
}
