package com.forero.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ParkingLot {
    private Long id;

    private String partnerId;

    private String parkingName;
}
