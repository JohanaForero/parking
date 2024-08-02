package com.forero.parking.infrastructure.exception;

import com.forero.parking.domain.exception.ParkingException;
import com.forero.parking.infrastructure.controller.ParkingController;
import com.forero.parking.openapi.model.ErrorObjectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.AbstractMap;
import java.util.Map;

@Slf4j
@ControllerAdvice(assignableTypes = ParkingController.class)
public class ParkingControllerAdvice {
    private static final String LOGGER_PREFIX = String.format("[%s]", EntranceControllerAdvice.class.getSimpleName());
    private static final Map<Class<? extends RuntimeException>, HttpStatus> HTTP_STATUS_BY_RUNTIME_EXCEPTION_CLASS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(ParkingException.ParkingNameAlreadyExistsException.class, HttpStatus.BAD_REQUEST));

    @ExceptionHandler(ParkingException.class)
    public ResponseEntity<ErrorObjectDto> handlerParkingException(final ParkingException parkingException) {
        log.error("{} [handlerParkingException] Caught exception", LOGGER_PREFIX, parkingException);

        final ErrorObjectDto errorObjectDto = new ErrorObjectDto();
        errorObjectDto.message(parkingException.getMessage());

        final HttpStatus httpStatus = HTTP_STATUS_BY_RUNTIME_EXCEPTION_CLASS.getOrDefault(parkingException.getClass(),
                HttpStatus.NOT_EXTENDED);

        return new ResponseEntity<>(errorObjectDto, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorObjectDto> handlerException(final Exception exception) {
        log.error("{} [handlerException] Unhandled exception", LOGGER_PREFIX, exception);

        final ErrorObjectDto errorObjectDto = new ErrorObjectDto();
        errorObjectDto.message(exception.getMessage());

        return new ResponseEntity<>(errorObjectDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
