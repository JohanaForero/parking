package com.forero.parking.domain.agregate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleAgregate {
    private Long vehicleId;
    private String licensePlate;
    private Boolean isFirstTime;
}
