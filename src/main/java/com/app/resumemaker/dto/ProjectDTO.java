package com.app.resumemaker.dto;

import java.util.List;
import java.util.ArrayList;

public class ProjectDTO {
    private String name;
    private String duration;
    private String technologies;
    private List<String> description = new ArrayList<>(); // front-end convenience, mapped to single TEXT in DB
    private String link;

    // ---------- Getters & Setters ----------
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTechnologies() {
        return technologies;
    }
    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public List<String> getDescription() {
        return description;
    }
    public void setDescription(List<String> description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    // ---------- Utility Methods ----------
    /**
     * Converts description list into a single string for database storage
     */
    public String getDescriptionAsText() {
        return String.join("\n", this.description);
    }

    /**
     * Populates description list from a single text string from DB
     */
    public void setDescriptionFromText(String text) {
        if (text != null && !text.isEmpty()) {
            this.description = List.of(text.split("\n"));
        } else {
            this.description = new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        return "ProjectDTO [name=" + name + ", duration=" + duration + ", technologies=" + technologies
                + ", description=" + description + ", link=" + link + "]";
    }
}
