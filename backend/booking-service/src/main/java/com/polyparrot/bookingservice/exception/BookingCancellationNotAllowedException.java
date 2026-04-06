package com.polyparrot.bookingservice.exception;

public class BookingCancellationNotAllowedException extends RuntimeException {
    public BookingCancellationNotAllowedException(String message) {
        super(message);
    }
}