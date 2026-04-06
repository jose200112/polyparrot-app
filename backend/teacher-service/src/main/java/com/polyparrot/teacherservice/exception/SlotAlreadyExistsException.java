package com.polyparrot.teacherservice.exception;

public class SlotAlreadyExistsException extends RuntimeException {
    public SlotAlreadyExistsException() {
        super("Ya existe un slot en ese horario");
    }
}