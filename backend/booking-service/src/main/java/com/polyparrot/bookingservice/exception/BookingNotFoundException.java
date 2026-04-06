package com.polyparrot.bookingservice.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException() {
        super("Reserva no encontrada");
    }
}