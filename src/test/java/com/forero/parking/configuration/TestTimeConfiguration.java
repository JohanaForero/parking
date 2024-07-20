package com.forero.parking.configuration;

import com.forero.parking.application.configuration.TimeConfiguration;

import java.time.LocalDateTime;

public class TestTimeConfiguration implements TimeConfiguration {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.of(2024, 7, 20, 18, 30, 0);
    }
}
