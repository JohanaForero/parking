package com.forero.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class ParkingLot {
    private Long id;

    private Vehicle vehicle;

    private LocalDateTime entranceDate;

    private String partnerId;

    private String parkingName;
}
