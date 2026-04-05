package com.polyparrot.teacherservice.exception;

public class CannotDeleteSlotException extends RuntimeException { 
	
	public CannotDeleteSlotException() { 
		super("Cannot delete slot with active bookings"); 
	} 
	
}
