package com.app.resumemaker.model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
public class Resume {

   
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    private int templateId;

    // ---------- Relationships ----------
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "basic_info_id", referencedColumnName = "id")
    private BasicInfoEntity basicInfo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Experience> experiences = new HashSet<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Education> educationList = new HashSet<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProjectDetails> projects = new HashSet<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Certification> certifications = new HashSet<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Skill> skills = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String experienceSummary;

    // ---------- Getters & Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getTemplateId() { return templateId; }
    public void setTemplateId(int templateId) { this.templateId = templateId; }

    public BasicInfoEntity getBasicInfo() { return basicInfo; }
    public void setBasicInfo(BasicInfoEntity basicInfo) { this.basicInfo = basicInfo; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Set<Experience> getExperiences() { return experiences; }

    public Set<Education> getEducationList() { return educationList; }

    public Set<ProjectDetails> getProjects() { return projects; }

    public Set<Certification> getCertifications() { return certifications; }

    public Set<Skill> getSkills() { return skills; }

    public String getExperienceSummary() { return experienceSummary; }
    public void setExperienceSummary(String experienceSummary) { this.experienceSummary = experienceSummary; }

    // ---------- Helper Methods ----------
    public void addExperience(Experience e) { experiences.add(e); e.setResume(this); }
    public void addEducation(Education e) { educationList.add(e); e.setResume(this); }
    public void addProject(ProjectDetails p) { projects.add(p); p.setResume(this); }
    public void addCertification(Certification c) { certifications.add(c); c.setResume(this); }
    public void addSkill(Skill s) { skills.add(s); s.setResume(this); }
    public void removeSkill(Skill s) { skills.remove(s); s.setResume(null); }
}
