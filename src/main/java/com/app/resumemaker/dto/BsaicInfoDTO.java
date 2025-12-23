package com.app.resumemaker.dto;

public class BsaicInfoDTO {

    private String name;
    private String title;
    private String summary;
    private String skills;
    private Object styleConfig; // Stores arbitrary style config JSON
    private ContactDTO contact; // DTO for the related ContactEntity
    

    // ---------- Getters & Setters ----------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public ContactDTO getContact() {
        return contact;
    }

    public Object getStyleConfig() {
        return styleConfig;
    }

    public void setStyleConfig(Object styleConfig) {
        this.styleConfig = styleConfig;
    }

    public void setContact(ContactDTO contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "BasicInfoDTO [name=" + name + ", title=" + title + ", summary=" + summary
                + ", skills=" + skills + ", contact=" + contact + "]";
    }
}
