package com.app.resumemaker.dto;

public class ResumeDTO {
	
	 private String name;
	    private String email;
	    private String phone;
	    
	    
	    public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getUniversity() {
			return university;
		}
		public void setUniversity(String university) {
			this.university = university;
		}
		public String getPassoutYear() {
			return passoutYear;
		}
		public void setPassoutYear(String passoutYear) {
			this.passoutYear = passoutYear;
		}
		public String getStream() {
			return stream;
		}
		public void setStream(String stream) {
			this.stream = stream;
		}
		@Override
		public String toString() {
			return "ResumeDTO [name=" + name + ", email=" + email + ", phone=" + phone + ", university=" + university
					+ ", passoutYear=" + passoutYear + ", stream=" + stream + ", experience=" + experience + ", skills="
					+ skills + "]";
		}
		public String getExperience() {
			return experience;
		}
		public void setExperience(String experience) {
			this.experience = experience;
		}
		public String getSkills() {
			return skills;
		}
		public void setSkills(String skills) {
			this.skills = skills;
		}
		private String university;
	    private String passoutYear;
	    private String stream;
	    private String experience;
	    private String skills;

}
