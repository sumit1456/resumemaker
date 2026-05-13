package com.app.resumemaker.dto;

public class CustomSectionDTO {
    private String title;
    private Object sectionData; // Can be whatever structure the frontend sends

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getSectionData() {
        return sectionData;
    }

    @Override
	public String toString() {
		return "CustomSectionDTO [title=" + title + ", sectionData=" + sectionData + "]";
	}

	public void setSectionData(Object sectionData) {
        this.sectionData = sectionData;
    }
}
