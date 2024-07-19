package com.forero.parking.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodeException {
    INVALID_PARAMETERS("Invalid %s parameters");
    private final String messageFormat;
}
