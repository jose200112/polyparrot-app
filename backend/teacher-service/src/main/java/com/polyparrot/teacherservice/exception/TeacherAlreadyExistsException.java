package com.polyparrot.teacherservice.exception;

public class TeacherAlreadyExistsException extends RuntimeException {

	public TeacherAlreadyExistsException() { 
		super("El profesor ya existe"); 
	} 
}
