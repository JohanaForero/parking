package com.forero.parking.infrastructure.adapter;

import com.forero.parking.application.port.EmailServerPort;
import com.forero.parking.domain.exception.EmailException;
import com.forero.parking.domain.model.Email;
import com.forero.parking.infrastructure.adapter.dto.EmailRequestDto;
import com.forero.parking.infrastructure.adapter.dto.EmailResponseDto;
import com.forero.parking.infrastructure.mapper.EmailMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

@Repository
@RequiredArgsConstructor
public class MicroserviceEmailAdapter implements EmailServerPort {
    private final RestClient emailRestClient;
    private final EmailMapper emailMapper;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    @Value("${email.endpoint.send-email}")
    private String sendEmailEndpoint;

    @Override
    public String sendEmail(final Email email) {
        final EmailRequestDto emailRequestDto = this.emailMapper.toDto(email);

        final ResponseEntity<EmailResponseDto> responseEntity;
        try {
            responseEntity = Retry.decorateCheckedSupplier(this.retry,
                            () -> CircuitBreaker.decorateCheckedSupplier(this.circuitBreaker,
                                            () -> this.emailRestClient
                                                    .post()
                                                    .uri(this.sendEmailEndpoint)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .body(emailRequestDto)
                                                    .retrieve()
                                                    .toEntity(EmailResponseDto.class))
                                    .get())
                    .get();
        } catch (final Throwable e) {
            throw new EmailException.EmailServerException("Error sending email", e);
        }

        final EmailResponseDto emailResponseDto = responseEntity.getBody();
        return emailResponseDto != null ? emailResponseDto.getMensaje() : null;
    }
}
