package com.polyparrot.teacherservice.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorResponse {
	private int status;
	private String message;
	private String timestamp;
	
	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now().toString();
	}
}
