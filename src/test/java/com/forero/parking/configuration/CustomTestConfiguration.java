package com.forero.parking.configuration;

import com.forero.parking.application.configuration.TimeConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class CustomTestConfiguration {
    
    @Bean()
    @Profile("test")
    TimeConfiguration timeConfiguration() {
        return new TestTimeConfiguration();
    }
}
