package com.forero.parking.infrastructure.configuration;

import com.forero.parking.application.configuration.GlobalConfiguration;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Setter
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "parking.global")
public class GlobalConfigurationProperties implements GlobalConfiguration {
    private String costPerHour;
    private Integer numberOfParkingLots;

    public BigDecimal getCostPerHour() {
        final String costPerHourWithDefault = this.costPerHour != null ? this.costPerHour : "0";
        return new BigDecimal(costPerHourWithDefault);
    }

    public int getNumberOfParkingLots() {
        return this.numberOfParkingLots != null ? this.numberOfParkingLots : 0;
    }
}
