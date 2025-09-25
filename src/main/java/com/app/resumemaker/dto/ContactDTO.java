package com.app.resumemaker.dto;

public class ContactDTO {
    private String phone;
    private String email;
    private String linkedin;
    private String github;
    private String location;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
