package com.forero.parking.domain.exception;

import java.io.Serial;

public class EntranceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1355614985629589277L;

    public EntranceException(final String message) {
        super(message);
    }

    public static class NotFoundParkingLotException extends EntranceException {

        @Serial
        private static final long serialVersionUID = -2951405768748327677L;

        public NotFoundParkingLotException(final String message) {
            super(message);
        }
    }

    public static class NotFoundParkingException extends EntranceException {
        @Serial
        private static final long serialVersionUID = 3128701387710406914L;

        public NotFoundParkingException(final String message) {
            super(message);
        }
    }

    public static class VehicleInsideException extends EntranceException {

        @Serial
        private static final long serialVersionUID = 8135210159210211008L;

        public VehicleInsideException(final String message) {
            super(message);
        }
    }

    public static class OccupiedException extends EntranceException {

        @Serial
        private static final long serialVersionUID = 4259048927048259772L;

        public OccupiedException(final String message) {
            super(message);
        }
    }
}
