package com.polyparrot.bookingservice.exception;

public class SlotAlreadyBookedException extends RuntimeException {
    public SlotAlreadyBookedException() {
        super("Slot already booked");
    }
}