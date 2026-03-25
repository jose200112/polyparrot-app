package com.polyparrot.bookingservice.exception;

public class InvalidBookingTimeException extends RuntimeException {
    public InvalidBookingTimeException() {
        super("Booking must be at least 24h in advance");
    }
}
