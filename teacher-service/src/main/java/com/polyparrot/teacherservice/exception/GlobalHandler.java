package com.polyparrot.teacherservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandler {

	@ExceptionHandler(CannotDeleteSlotException.class) 
	public ResponseEntity<ErrorResponse> handleCannotDelete(CannotDeleteSlotException ex) { 
		log.warn("Attempt to delete slot with bookings"); 
		
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, ex.getMessage())); 
	}
}
