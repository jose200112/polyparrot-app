package com.polyparrot.bookingservice.exception;

public class SlotNotAvailableException extends RuntimeException {
    public SlotNotAvailableException() {
        super("Slot not available");
    }
}