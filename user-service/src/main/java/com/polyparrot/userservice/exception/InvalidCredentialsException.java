package com.polyparrot.userservice.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciales incorrectas");
    }
}