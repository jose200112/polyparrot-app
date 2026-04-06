package com.polyparrot.teacherservice.exception;

public class TeacherNotFoundException extends RuntimeException {
    public TeacherNotFoundException() {
        super("Profesor no encontrado");
    }
}