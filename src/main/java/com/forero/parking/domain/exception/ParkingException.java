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

    public static class ParkingNoFoundException extends ParkingException {
        @Serial
        private static final long serialVersionUID = 8636346412341375029L;

        public ParkingNoFoundException(final String message) {
            super(message);
        }
    }

    public static class UserNoAuthorized extends ParkingException {
        @Serial
        private static final long serialVersionUID = 4785244121827151795L;

        public UserNoAuthorized(final String message) {
            super(message);
        }
    }

    public static class EmptyList extends ParkingException {
        @Serial
        private static final long serialVersionUID = 5156223981447822750L;

        public EmptyList(final String message) {
            super(message);
        }
    }
}
