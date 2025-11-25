package com.app.resumemaker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "experiences")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String position;
    private String company;
    private String location;
    private String duration;

    @Lob
    @Column(columnDefinition = "TEXT") // multiple achievements stored as newline-separated text
    private String achievements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    // ---------- Constructors ----------
    public Experience() {}

    public Experience(String position, String company, String location, String duration, String achievements, Resume resume) {
        this.position = position;
        this.company = company;
        this.location = location;
        this.duration = duration;
        this.achievements = achievements;
        this.resume = resume;
    }

    // ---------- Getters & Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    // ---------- Helper Methods ----------
    /**
     * Returns achievements as a single string.
     * Frontend can split by newline if needed.
     */
    public String getAchievementsAsText() {
        return achievements != null ? achievements : "";
    }

    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id +
                ", position='" + position + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", duration='" + duration + '\'' +
                ", achievements='" + achievements + '\'' +
                ", resumeId=" + (resume != null ? resume.getId() : null) +
                '}';
    }
}
