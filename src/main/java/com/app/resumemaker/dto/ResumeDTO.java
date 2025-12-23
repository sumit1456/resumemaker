package com.app.resumemaker.dto;

import java.util.List;

import com.app.resumemaker.model.User;

public class ResumeDTO {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private BsaicInfoDTO details; // Basic info including name, title, summary, skills
    private ContactDTO contact; // Contact details
    private List<ExperienceDTO> experiences;
    private List<ProjectDTO> projects;
    private List<EducationDTO> educationList;

    private List<CertificationsDTO> certifications;

    private List<CustomSectionDTO> customSections; // New field for custom sections
    private java.util.Map<String, String> sectionTitles; // New field for custom titles

    private int templateId;

    public int getTemplateId() {
        return templateId;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // ✅ Added: skills to match the Skill entity
    private List<SkillDTO> skills;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // --- Getters & Setters ---
    public BsaicInfoDTO getDetails() {
        return details;
    }

    public void setDetails(BsaicInfoDTO details) {
        this.details = details;
    }

    public ContactDTO getContact() {
        return contact;
    }

    public List<CustomSectionDTO> getCustomSections() {
        return customSections;
    }

    public void setCustomSections(List<CustomSectionDTO> customSections) {
        this.customSections = customSections;
    }

    public java.util.Map<String, String> getSectionTitles() {
        return sectionTitles;
    }

    public void setSectionTitles(java.util.Map<String, String> sectionTitles) {
        this.sectionTitles = sectionTitles;
    }

    public void setContact(ContactDTO contact) {
        this.contact = contact;
    }

    public List<ExperienceDTO> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<ExperienceDTO> experiences) {
        this.experiences = experiences;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }

    public List<EducationDTO> getEducationList() {
        return educationList;
    }

    public void setEducationList(List<EducationDTO> educationList) {
        this.educationList = educationList;
    }

    public List<CertificationsDTO> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<CertificationsDTO> certifications) {
        this.certifications = certifications;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    

    @Override
	public String toString() {
		return "ResumeDTO [customSections=" + customSections + ", sectionTitles=" + sectionTitles + "]";
	}

	public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

}
