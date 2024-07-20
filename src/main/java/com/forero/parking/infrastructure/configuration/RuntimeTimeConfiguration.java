package com.forero.parking.infrastructure.configuration;

import com.forero.parking.application.configuration.TimeConfiguration;

import java.time.LocalDateTime;

public class RuntimeTimeConfiguration implements TimeConfiguration {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
