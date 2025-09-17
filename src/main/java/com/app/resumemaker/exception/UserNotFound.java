package com.app.resumemaker.exception;

public class UserNotFound extends RuntimeException {

	
	public String getMessage() {
		return "User does not exist";
	}
}
