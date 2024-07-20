package com.forero.parking.infrastructure.configuration;

import com.forero.parking.application.configuration.TimeConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class CustomConfiguration {

    @Bean()
    @Profile("!test")
    TimeConfiguration timeConfiguration() {
        return new RuntimeTimeConfiguration();
    }
}
