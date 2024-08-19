package com.forero.parking.infrastructure.controller;

import com.forero.parking.application.service.EmailService;
import com.forero.parking.infrastructure.adapter.gateways.Email;
import com.forero.parking.infrastructure.mapper.EmailMapper;
import com.forero.parking.openapi.api.EmailApi;
import com.forero.parking.openapi.model.ParkingEmailRequestDto;
import com.forero.parking.openapi.model.ParkingEmailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.aPIDocumentation.base-path}")
public class EmailController implements EmailApi {
    private final EmailService emailService;
    private final EmailMapper emailMapper;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','PARTNER')")
    public ResponseEntity<ParkingEmailResponseDto> sendEmail(final ParkingEmailRequestDto parkingEmailRequestDto) {
        final Email email = this.emailMapper.toDomain(parkingEmailRequestDto);

        final String response = this.emailService.sendEmail(email);

        return new ResponseEntity<>(this.emailMapper.toDto(response), HttpStatus.OK);
    }
}
