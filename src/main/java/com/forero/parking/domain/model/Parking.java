package com.forero.parking.domain.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Parking {
    private Long id;

    private String name;

    private String partnerId;

    private int costPerHour;

    private int numberOfParkingLots;
}
