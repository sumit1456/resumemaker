package com.app.resumemaker.dto;

public class SkillDTO {

    private Long id;        // optional when creating new skills
    private String name;

    // ----- Constructors -----
    public SkillDTO() {
    }

    public SkillDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public SkillDTO(String name) {
        this.name = name;
    }

    // ----- Getters & Setters -----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SkillDTO{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
