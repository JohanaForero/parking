package com.forero.parking.infrastructure.adapter.gateways;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Email {
    private String address;

    private String licensePlate;

    private String message;

    private String parkingName;
}
