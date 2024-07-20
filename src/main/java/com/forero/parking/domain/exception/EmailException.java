package com.forero.parking.domain.exception;

import java.io.Serial;

public class EmailException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6515024472842439655L;

    public EmailException(final String message) {
        super(message);
    }

    public EmailException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static class VehicleNotInsideException extends EmailException {

        @Serial
        private static final long serialVersionUID = -4124384935274250514L;

        public VehicleNotInsideException(final String message) {
            super(message);
        }
    }

    public static class EmailServerException extends EmailException {

        @Serial
        private static final long serialVersionUID = -701523988522103063L;

        public EmailServerException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
