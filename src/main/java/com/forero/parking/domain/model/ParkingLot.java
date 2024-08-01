package com.forero.parking.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ParkingLot {
    private Long id;

    private Vehicle vehicle;

    private LocalDateTime entranceDate;

    private String name;

    private String partner;
}
