package com.forero.parking.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class DetailRegisterVehicle {
    private Long id;
    private ParkingLot parkingLot;
    private Vehicle vehicle;
    private LocalDateTime entranceDate;
}
