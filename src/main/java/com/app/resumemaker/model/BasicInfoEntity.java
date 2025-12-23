package com.app.resumemaker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "basic_info")
public class BasicInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private ContactEntity contact;

    @Column(columnDefinition = "TEXT")
    private String sectionTitles; // Stores JSON string of title map

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "style_config_id", referencedColumnName = "id")
    private StyleConfigEntity styleConfig;

    // Getters and Setters
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

    public ContactEntity getContact() {
        return contact;
    }

    public void setContact(ContactEntity contact) {
        this.contact = contact;
    }

    public String getSectionTitles() {
        return sectionTitles;
    }

    public void setSectionTitles(String sectionTitles) {
        this.sectionTitles = sectionTitles;
    }

    public StyleConfigEntity getStyleConfig() {
        return styleConfig;
    }

    public void setStyleConfig(StyleConfigEntity styleConfig) {
        this.styleConfig = styleConfig;
    }
}
