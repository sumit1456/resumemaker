package com.app.resumemaker.dto;

public class EducationDTO {

	 private String degree;
	    private String institution;
	    private String location;
	    private String year;
	    private String gpa;
	    
	    
		@Override
		public String toString() {
			return "EducationDTO [degree=" + degree + ", institution=" + institution + ", location=" + location
					+ ", year=" + year + ", gpa=" + gpa + "]";
		}
		public String getDegree() {
			return degree;
		}
		public void setDegree(String degree) {
			this.degree = degree;
		}
		public String getInstitution() {
			return institution;
		}
		public void setInstitution(String institution) {
			this.institution = institution;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public String getGpa() {
			return gpa;
		}
		public void setGpa(String gpa) {
			this.gpa = gpa;
		}
}
