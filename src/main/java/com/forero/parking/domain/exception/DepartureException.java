package com.forero.parking.domain.exception;

import java.io.Serial;

public class DepartureException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3754501296622554709L;

    public DepartureException(final String message) {
        super(message);
    }

    public static class VehicleNotInParkingException extends DepartureException {

        @Serial
        private static final long serialVersionUID = 8181393423130231881L;

        public VehicleNotInParkingException(final String message) {
            super(message);
        }
    }

    public static class VehicleNoFound extends DepartureException {
        @Serial
        private static final long serialVersionUID = 1871647130130458581L;

        public VehicleNoFound(final String message) {
            super(message);
        }
    }
}
