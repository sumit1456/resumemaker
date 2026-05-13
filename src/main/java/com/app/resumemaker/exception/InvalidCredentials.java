package com.app.resumemaker.exception;

public class InvalidCredentials extends RuntimeException{
	public String getMessage() {
		return "Authentication failed. Please check your login credentials.";
	}
}
