package com.polyparrot.bookingservice.exception;

public class BookingNotPendingException extends RuntimeException {
    public BookingNotPendingException() {
        super("Solo se pueden confirmar reservas pendientes");
    }
}