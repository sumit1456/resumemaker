package com.app.resumemaker.exception;

public class UserNotFound extends RuntimeException {

	private String msg;
	public UserNotFound(String string) {
		msg = string;
	}

	public String getMessage() {
		return msg;
	}
}
