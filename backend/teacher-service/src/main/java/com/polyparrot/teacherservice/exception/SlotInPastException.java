package com.polyparrot.teacherservice.exception;

public class SlotInPastException extends RuntimeException {
    public SlotInPastException() {
        super("No puedes crear slots en el pasado");
    }
}