package com.app.resumemaker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_details")
public class ProjectDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name", nullable = false, length = 150)
    private String projectName;

    /**
     * A long free-text description (can include newlines or Markdown).
     * Stored as a TEXT column.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "tech_stack", length = 255)
    private String techStack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    // ---------- Constructors ----------
    public ProjectDetails() {
    }

    public ProjectDetails(String projectName, String description, String techStack, Resume resume) {
        this.projectName = projectName;
        this.description = description;
        this.techStack = techStack;
        this.resume = resume;
    }

    // ---------- Getters & Setters ----------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    @Override
    public String toString() {
        return "ProjectDetails{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                ", techStack='" + techStack + '\'' +
                ", resumeId=" + (resume != null ? resume.getId() : null) +
                '}';
    }

	
	
	@Column(name = "link")
	private String link;

	public String getLink() {
	    return link;
	}

	public void setLink(String link) {
	    this.link = link;
	}
}
