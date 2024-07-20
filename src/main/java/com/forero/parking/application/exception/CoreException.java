package com.forero.parking.application.exception;

import com.forero.parking.domain.exception.CodeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Slf4j
public class CoreException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 7574683090178603830L;

    private final CodeException codeException;

    protected CoreException(final CodeException codeException, final Exception exception, final String... fields) {
        super(getMessage(codeException, fields), exception);
        this.codeException = codeException;
    }

    private static String getMessage(final CodeException codeException, final String... fields) {
        return fields.length > 0
                ? String.format(codeException.getMessageFormat(), (Object[]) fields)
                : codeException.getMessageFormat();
    }
}
