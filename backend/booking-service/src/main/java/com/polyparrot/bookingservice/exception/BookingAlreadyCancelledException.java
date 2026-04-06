package com.polyparrot.bookingservice.exception;

public class BookingAlreadyCancelledException extends RuntimeException {
    public BookingAlreadyCancelledException() {
        super("La reserva ya está cancelada");
    }
}