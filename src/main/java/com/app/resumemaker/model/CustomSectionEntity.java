package com.app.resumemaker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_sections")
public class CustomSectionEntity {

    @Override
	public String toString() {
		return "CustomSectionEntity [id=" + id + ", sectionData=" + sectionData + ", title=" + title;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String sectionData;

    // Optional: title if needed separate from data, but typically bundled
    private String title;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionData() {
        return sectionData;
    }

    public void setSectionData(String sectionData) {
        this.sectionData = sectionData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}
