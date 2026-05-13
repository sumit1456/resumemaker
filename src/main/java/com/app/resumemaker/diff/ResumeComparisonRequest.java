package com.app.resumemaker.diff;

import java.util.Map;



public class ResumeComparisonRequest {
    private Map<String, Object> oldResume; // summary, projects, experience
    private Map<String, Object> newResume;

    // Getters and setters
    public Map<String, Object> getOldResume() { return oldResume; }
    public void setOldResume(Map<String, Object> oldResume) { this.oldResume = oldResume; }

    public Map<String, Object> getNewResume() { return newResume; }
    public void setNewResume(Map<String, Object> newResume) { this.newResume = newResume; }
}

