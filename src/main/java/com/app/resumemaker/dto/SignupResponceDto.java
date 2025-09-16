package com.app.resumemaker.dto;

public class SignupResponceDto {
	
	private String message;
	private long userid;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public SignupResponceDto(String message, Long long1) {
		super();
		this.message = message;
		this.userid = long1;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	

}
