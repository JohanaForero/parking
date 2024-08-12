package com.forero.parking.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ParkingLot {
    private Long id;

    private LocalDateTime entranceDate;

    private Parking parking;

    private int code;

    public Long getParkingId() {
        return this.parking.getId();
    }
}
