package com.polyparrot.teacherservice.exception;

public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException() {
        super("Slot no encontrado");
    }
}