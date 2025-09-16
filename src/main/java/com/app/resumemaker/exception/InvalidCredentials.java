package com.app.resumemaker.exception;

public class InvalidCredentials extends RuntimeException{
	public String getMessage() {
		return "Please Enter correct credentials";
	}
}
