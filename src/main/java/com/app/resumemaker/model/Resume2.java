//package com.app.resumemaker.model;
//
//import java.util.ArrayList;
//import java.util.List;
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "resumes")
//public class Resume2 {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(length = 100)
//    private String title;
//
//    private int templateId;
//
//    // ---------- Relationships ----------
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "basic_info_id", referencedColumnName = "id")
//    private BasicInfoEntity basicInfo;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Experience> experiences = new ArrayList<>();
//
//    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Education> educationList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<ProjectDetails> projects = new ArrayList<>();
//
//    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Certification> certifications = new ArrayList<>();
//
//    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Skill> skills = new ArrayList<>();
//
//    @Column(columnDefinition = "TEXT")
//    private String experienceSummary;
//
//    // ---------- Getters & Setters ----------
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; }
//
//    public int getTemplateId() { return templateId; }
//    public void setTemplateId(int templateId) { this.templateId = templateId; }
//
//    public BasicInfoEntity getBasicInfo() { return basicInfo; }
//    public void setBasicInfo(BasicInfoEntity basicInfo) { this.basicInfo = basicInfo; }
//
//    public User getUser() { return user; }
//    public void setUser(User user) { this.user = user; }
//
//    public List<Experience> getExperiences() { return experiences; }
//    public void setExperiences(List<Experience> experiences) {
//        this.experiences = experiences;
//        experiences.forEach(e -> e.setResume(this));
//    }
//
//    public List<Education> getEducationList() { return educationList; }
//    public void setEducationList(List<Education> educationList) {
//        this.educationList = educationList;
//        educationList.forEach(e -> e.setResume(this));
//    }
//
//    public List<ProjectDetails> getProjects() { return projects; }
//    public void setProjects(List<ProjectDetails> projects) {
//        this.projects = projects;
//        projects.forEach(p -> p.setResume(this));
//    }
//
//    public List<Certification> getCertifications() { return certifications; }
//    public void setCertifications(List<Certification> certifications) {
//        this.certifications = certifications;
//        certifications.forEach(c -> c.setResume(this));
//    }
//
//    public List<Skill> getSkills() { return skills; }
//    public void setSkills(List<Skill> skills) {
//        this.skills = skills;
//        skills.forEach(s -> s.setResume(this));
//    }
//
//    public String getExperienceSummary() { return experienceSummary; }
//    public void setExperienceSummary(String experienceSummary) { this.experienceSummary = experienceSummary; }
//
//    // ---------- Helper Methods ----------
//    public void addExperience(Experience e) { experiences.add(e); e.setResume(this); }
//    public void addEducation(Education e) { educationList.add(e); e.setResume(this); }
//    public void addProject(ProjectDetails p) { projects.add(p); p.setResume(this); }
//    public void addCertification(Certification c) { certifications.add(c); c.setResume(this); }
//    public void addSkill(Skill s) { skills.add(s); s.setResume(this); }
//    public void removeSkill(Skill s) { skills.remove(s); s.setResume(null); }
//}
