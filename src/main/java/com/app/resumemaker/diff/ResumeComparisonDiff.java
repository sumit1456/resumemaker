package com.app.resumemaker.diff;

import java.util.Map;

public class ResumeComparisonDiff {
    private Map<String, Object> oldResume;
    private Map<String, Object> newResume;
    private Map<String, Object> differences; // FIXED

    public Map<String, Object> getOldResume() { return oldResume; }
    public void setOldResume(Map<String, Object> oldResume) { this.oldResume = oldResume; }

    public Map<String, Object> getNewResume() { return newResume; }
    public void setNewResume(Map<String, Object> newResume) { this.newResume = newResume; }

    public Map<String, Object> getDifferences() { return differences; }
    public void setDifferences(Map<String, Object> differences) { this.differences = differences; }

    @Override
    public String toString() {
        return "ResumeComparisonDiff [oldResume=" + oldResume + ", newResume=" + newResume + ", differences=" + differences + "]";
    }
}
