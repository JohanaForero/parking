package com.forero.parking.infrastructure.exception;

import com.forero.parking.domain.exception.EmailException;
import com.forero.parking.infrastructure.controller.EmailController;
import com.forero.parking.openapi.model.ErrorObjectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

@Slf4j
@ControllerAdvice(assignableTypes = EmailController.class)
public class EmailControllerAdvice {
    private static final String LOGGER_PREFIX = String.format("[%s]", EmailControllerAdvice.class.getSimpleName());

    private static final Map<Class<? extends RuntimeException>, HttpStatus> HTTP_STATUS_BY_RUNTIME_EXCEPTION_CLASS = Map.ofEntries(
            new SimpleEntry<>(EmailException.VehicleNotInsideException.class, HttpStatus.BAD_REQUEST),
            new SimpleEntry<>(EmailException.EmailServerException.class, HttpStatus.INTERNAL_SERVER_ERROR));

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorObjectDto> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException httpMessageNotReadableException) {
        log.error("{} [handleHttpMessageNotReadableException] Caught exception", LOGGER_PREFIX,
                httpMessageNotReadableException);

        final ErrorObjectDto errorObjectDto = new ErrorObjectDto();
        errorObjectDto.setMessage("Required request body");

        return new ResponseEntity<>(errorObjectDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObjectDto> handlerHttpMessageNotReadableException(
            final MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error("{} [handlerHttpMessageNotReadableException] Caught exception", LOGGER_PREFIX,
                methodArgumentNotValidException);

        final FieldError fieldError = methodArgumentNotValidException.getFieldErrors().getFirst();

        final ErrorObjectDto errorObjectDto = new ErrorObjectDto();
        errorObjectDto.message(String.format("Invalid %s parameters", fieldError.getField()));

        return new ResponseEntity<>(errorObjectDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorObjectDto> handlerEmailException(final EmailException emailException) {
        log.error("{} [handlerEmailException] Caught exception", LOGGER_PREFIX, emailException);

        final ErrorObjectDto errorObjectDto = new ErrorObjectDto();
        errorObjectDto.message(emailException.getMessage());

        final HttpStatus httpStatus = HTTP_STATUS_BY_RUNTIME_EXCEPTION_CLASS.getOrDefault(emailException.getClass(),
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
