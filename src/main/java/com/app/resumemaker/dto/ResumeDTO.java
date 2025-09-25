package com.app.resumemaker.dto;

import java.util.List;

import com.app.resumemaker.model.User;

public class ResumeDTO {

    private BsaicInfoDTO details;          // Basic info including name, title, summary, skills
    private ContactDTO contact;            // Contact details
    private List<ExperienceDTO> experiences;
    private List<ProjectDTO> projects;
    private List<EducationDTO> educationList;
    private List<CertificationsDTO> certifications;
    private Long userId;

    

	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
    public String toString() {
        return "ResumeDTO [details=" + details + ", contact=" + contact + ", experiences=" + experiences
                + ", projects=" + projects + ", educationList=" + educationList + ", certifications=" + certifications + "]";
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

	
}
