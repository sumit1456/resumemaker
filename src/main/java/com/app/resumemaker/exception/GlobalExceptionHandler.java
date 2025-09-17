package com.app.resumemaker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

	
	public ResponseEntity<String> userNotFound(UserNotFound unf){
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(unf.getMessage());
	}
}
