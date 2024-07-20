package com.forero.parking.application.configuration;

import java.math.BigDecimal;

public interface GlobalConfiguration {
    BigDecimal getCostPerHour();

    int getNumberOfParkingLots();
}
