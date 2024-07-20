package com.forero.parking.domain.exception;

import java.io.Serial;

public class DepartureException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3754501296622554709L;

    public DepartureException(final String message) {
        super(message);
    }

    public static class VehicleNotInParkingLotException extends DepartureException {

        @Serial
        private static final long serialVersionUID = 8181393423130231881L;

        public VehicleNotInParkingLotException(final String message) {
            super(message);
        }
    }
}
