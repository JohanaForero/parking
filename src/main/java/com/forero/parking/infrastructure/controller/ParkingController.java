package com.forero.parking.infrastructure.controller;

import com.forero.parking.openapi.api.ParkingApi;
import com.forero.parking.openapi.model.LoginRequestDto;
import com.forero.parking.openapi.model.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParkingController implements ParkingApi {
    private static final String LOGGER_PREFIX = String.format("[%s] ", ParkingController.class.getSimpleName());

    @Override
    public ResponseEntity<UserResponseDto> logIn(LoginRequestDto loginRequestDto) {
        return ParkingApi.super.logIn(loginRequestDto);
    }
}
