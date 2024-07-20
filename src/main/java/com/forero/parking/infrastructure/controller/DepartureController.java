package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.ParkingLotService;
import com.forero.parking.domain.model.History;
import com.forero.parking.domain.model.ParkingLot;
import com.forero.parking.domain.model.Vehicle;
import com.forero.parking.infrastructure.mapper.HistoryMapper;
import com.forero.parking.infrastructure.mapper.ParkingLotMapper;
import com.forero.parking.infrastructure.mapper.VehicleMapper;
import com.forero.parking.openapi.api.DepartureApi;
import com.forero.parking.openapi.model.ParkingDepartureRequestDto;
import com.forero.parking.openapi.model.ParkingDepartureResponseDto;
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
    private final VehicleMapper vehicleMapper;
    private final HistoryMapper historyMapper;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<ParkingDepartureResponseDto> registerVehicleExit(final ParkingDepartureRequestDto parkingDepartureRequestDto) {
        final ParkingLot parkingLot = this.parkingLotMapper.toDomain(parkingDepartureRequestDto.getParkingLotId());
        final Vehicle vehicle = this.vehicleMapper.toDomain(parkingDepartureRequestDto.getLicensePlate());

        final History history = this.parkingLotService.registerVehicleExit(parkingLot, vehicle);

        return new ResponseEntity<>(this.historyMapper.toDepartureDto(history), HttpStatus.OK);
    }
}
