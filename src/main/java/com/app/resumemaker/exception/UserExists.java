package com.app.resumemaker.exception;

public class UserExists extends RuntimeException {
	
	public String getMessage() {
		return "User exists";
	}

}
