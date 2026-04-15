package com.polyparrot.bookingservice.exception;

public class StudentAlreadyBookedException extends RuntimeException {
 public StudentAlreadyBookedException() {
     super("Ya tienes una reserva en ese horario");
 }
}