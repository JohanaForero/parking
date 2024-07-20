package com.forero.parking.infrastructure.configuration;

import com.forero.parking.application.configuration.TimeConfiguration;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class CustomConfiguration {
    @Value("${email.server.url}")
    private String emailServerUrl;

    @Bean
    @Profile("!test")
    TimeConfiguration timeConfiguration() {
        return new RuntimeTimeConfiguration();
    }

    @Bean
    RestClient emailRestClient() {
        return RestClient.builder()
                .baseUrl(this.emailServerUrl)
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    @Bean
    public CircuitBreaker circuitBreaker(final CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker("emailCircuitBreaker");
    }

    @Bean
    public Retry retry() {
        final RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(2))
                .build();
        return Retry.of("emailRetry", config);
    }
}
