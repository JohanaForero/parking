package com.forero.parking.domain.exception;

import java.io.Serial;

public class ParkingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2544719615365718859L;

    public ParkingException(final String message) {
        super(message);
    }

    public static class ParkingNameAlreadyExistsException extends ParkingException {

        @Serial
        private static final long serialVersionUID = 5850797788206183261L;

        public ParkingNameAlreadyExistsException(final String message) {
            super(message);
        }
    }
}
