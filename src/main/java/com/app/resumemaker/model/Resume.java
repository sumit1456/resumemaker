package com.app.resumemaker.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "basic_info_id", referencedColumnName = "id")
    private BasicInfoEntity basicInfo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educationList = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectDetails> projects = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String experienceSummary; // optional text summary

    @Column(columnDefinition = "TEXT")
    private String skillsSummary; // optional text summary
  
   
    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BasicInfoEntity getBasicInfo() { return basicInfo; }
    public void setBasicInfo(BasicInfoEntity basicInfo) { this.basicInfo = basicInfo; }

  

    public List<Experience> getExperiences() { return experiences; }
    public void setExperiences(List<Experience> experiences) { 
        this.experiences = experiences; 
        experiences.forEach(exp -> exp.setResume(this)); // ensure bidirectional link
    }

    public List<Education> getEducationList() { return educationList; }
    public void setEducationList(List<Education> educationList) {
        this.educationList = educationList;
        educationList.forEach(ed -> ed.setResume(this));
    }

    public List<ProjectDetails> getProjects() { return projects; }
    public void setProjects(List<ProjectDetails> projects) { 
        this.projects = projects; 
        projects.forEach(p -> p.setResume(this));
    }

    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { 
        this.certifications = certifications; 
        certifications.forEach(c -> c.setResume(this));
    }

    public String getExperienceSummary() { return experienceSummary; }
    public void setExperienceSummary(String experienceSummary) { this.experienceSummary = experienceSummary; }

    public String getSkillsSummary() { return skillsSummary; }
    public void setSkillsSummary(String skillsSummary) { this.skillsSummary = skillsSummary; }
    
    // ===== Helper Methods =====
    public void addExperience(Experience exp) {
        experiences.add(exp);
        exp.setResume(this);
    }

    public void addEducation(Education edu) {
        educationList.add(edu);
        edu.setResume(this);
    }

    public void addProject(ProjectDetails project) {
        projects.add(project);
        project.setResume(this);
    }

    public void addCertification(Certification cert) {
        certifications.add(cert);
        cert.setResume(this);
    }
	public void setExperiences(String string) {
		// TODO Auto-generated method stub
		
	}
	
}
