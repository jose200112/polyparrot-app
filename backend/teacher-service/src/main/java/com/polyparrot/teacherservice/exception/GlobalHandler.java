package com.polyparrot.teacherservice.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandler {

	@ExceptionHandler(CannotDeleteSlotException.class)
	public ResponseEntity<ErrorResponse> handleCannotDelete(CannotDeleteSlotException ex) {
	    log.warn("Attempt to delete slot with bookings");
	    return ResponseEntity.status(HttpStatus.CONFLICT)
	        .body(new ErrorResponse(409, ex.getMessage()));
	}
	
	@ExceptionHandler(TeacherAlreadyExistsException.class) 
	public ResponseEntity<ErrorResponse> teacherAlreadyExists(TeacherAlreadyExistsException ex) { 
		log.warn("Intento de registar profesor"); 
		
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, ex.getMessage())); 
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getFieldErrors()
	        .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}
	
	@ExceptionHandler(TeacherNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleTeacherNotFound(TeacherNotFoundException ex) {
	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	        .body(new ErrorResponse(404, ex.getMessage()));
	}
	
	@ExceptionHandler(SlotInPastException.class)
	public ResponseEntity<ErrorResponse> handleSlotInPast(SlotInPastException ex) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	        .body(new ErrorResponse(400, ex.getMessage()));
	}

	@ExceptionHandler(SlotAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleSlotAlreadyExists(SlotAlreadyExistsException ex) {
	    return ResponseEntity.status(HttpStatus.CONFLICT)
	        .body(new ErrorResponse(409, ex.getMessage()));
	}

	@ExceptionHandler(SlotNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleSlotNotFound(SlotNotFoundException ex) {
	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	        .body(new ErrorResponse(404, ex.getMessage()));
	}
}
