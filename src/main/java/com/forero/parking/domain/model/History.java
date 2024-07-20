package com.forero.parking.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class History {
    private Long id;

    private Vehicle vehicle;

    private ParkingLot parkingLot;

    private LocalDateTime entranceDate;

    private LocalDateTime departureDate;

    private BigDecimal totalPaid;
}
