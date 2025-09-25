package com.app.resumemaker.dto;

import java.util.List;

public class ExperienceDTO {

    private String position;
    private String company;
    private String location;
    private String duration;
    private List<String> achievements; // multiple achievements

    public ExperienceDTO() {}

    public ExperienceDTO(String position, String company, String location, String duration, List<String> achievements) {
        this.position = position;
        this.company = company;
        this.location = location;
        this.duration = duration;
        this.achievements = achievements;
    }

    // --- Getters & Setters ---
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<String> achievements) {
        this.achievements = achievements;
    }

    @Override
    public String toString() {
        return "ExperienceDTO{" +
                "position='" + position + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", duration='" + duration + '\'' +
                ", achievements=" + achievements +
                '}';
    }
}
