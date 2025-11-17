package com.app.resumemaker.exception;

public class GroqApiException extends RuntimeException {

	
	private final String code;
    private final String type;

    public GroqApiException(String message, String code, String type) {
        super(message);
        this.code = code;
        this.type = type;
    }

    public String getCode() { return code; }
    public String getType() { return type; }
}
