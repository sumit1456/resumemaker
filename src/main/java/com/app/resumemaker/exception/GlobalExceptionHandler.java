package com.app.resumemaker.exception;

import java.util.Map;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(UserNotFound.class)
	 @ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<String> userNotFound(UserNotFound unf){
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(unf.getMessage());
	}
	 
	 @ExceptionHandler(UserExists.class)
	 @ResponseStatus(HttpStatus.CONFLICT)
	 public ResponseEntity<String> userExists(UserExists ue){
		 return ResponseEntity.status(HttpStatus.CONFLICT)
				 .body("Email Id already exists, Please continue to Log in");
	 }
	 
	 @ExceptionHandler(JDBCConnectionException.class)
		 public ResponseEntity<String> jdbcConnectionException(){
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("Problem in connecting database");
		 
	 }
	 
	 @ExceptionHandler(Exception.class)
	 public void catchAll(Exception e) {
	     e.printStackTrace();
	 }
	 
	 @ExceptionHandler(GroqApiException.class)
	 public ResponseEntity<?> handleGroqExceptions(GroqApiException ex) {

	     if ("rate_limit_exceeded".equals(ex.getCode())) {
	         return ResponseEntity.status(429)
	                 .body(Map.of(
	                     "error", "Groq rate limit reached. Try again later.",
	                     "details", ex.getMessage()
	                 ));
	     }

	     return ResponseEntity.status(400)
	             .body(Map.of(
	                 "error", "Groq API Error",
	                 "message", ex.getMessage()
	             ));
	 }


	 
}
