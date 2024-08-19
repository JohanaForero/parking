package com.forero.parking.infrastructure.adapter.gateways;

import com.forero.parking.application.port.EmailServerPort;
import com.forero.parking.domain.exception.EmailException;
import com.forero.parking.infrastructure.adapter.dto.EmailRequestDto;
import com.forero.parking.infrastructure.adapter.dto.EmailResponseDto;
import com.forero.parking.infrastructure.mapper.EmailMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
@RequiredArgsConstructor
public class MicroserviceEmailAdapter implements EmailServerPort {
    private final EmailMapper emailMapper;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final WebClient webClient;

    @Value("${base.url.micro.send-email}")
    private String sendEmailEndpoint;

    @Override
    public String sendEmail(final Email email) {
        final EmailRequestDto emailRequestDto = this.emailMapper.toDto(email);
        try {
            EmailResponseDto emailResponse = Retry.decorateCheckedSupplier(this.retry,
                    () -> CircuitBreaker.decorateCheckedSupplier(this.circuitBreaker,
                                    () -> webClient.post()
                                            .uri(sendEmailEndpoint)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(emailRequestDto)
                                            .retrieve()
                                            .bodyToMono(EmailResponseDto.class)
                                            .block())
                            .get()).get();

            return emailResponse != null ? emailResponse.getMensaje() : null;

        } catch (Throwable e) {
            throw new EmailException.EmailServerException("Error sending email", e);
        }
    }
}
