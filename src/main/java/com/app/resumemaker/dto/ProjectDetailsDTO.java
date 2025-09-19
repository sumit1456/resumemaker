package com.app.resumemaker.dto;   

public class ProjectDetailsDTO {

    private Long id;           
    private String projectName;
    private String description;
    private String techStack;   
 
    public ProjectDetailsDTO() {
    }

    public ProjectDetailsDTO(Long id, String projectName, String description, String techStack) {
        this.id = id;
        this.projectName = projectName;
        this.description = description;
        this.techStack = techStack;
    }

    // Getters & Setters
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
}
