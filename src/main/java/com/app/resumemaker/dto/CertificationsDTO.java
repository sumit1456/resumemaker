package com.app.resumemaker.dto;

public class CertificationsDTO {

	  @Override
	public String toString() {
		return "CertificationsDTO [name=" + name + ", getName()=" + getName() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	  private String name;

	  public String getName() {
		  return name;
	  }

	  public void setName(String name) {
		  this.name = name;
	  }
}
