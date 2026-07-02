package com.app.resumemaker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.resumemaker.annotation.APITime;
import com.app.resumemaker.diff.ResumeComparisonDiff;
import com.app.resumemaker.diff.ResumeComparisonRequest;
import com.app.resumemaker.dto.ResumeDTO;
import com.app.resumemaker.service.GroqAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AIServiceController {

    @Autowired
    private GroqAIService groqService;

    @APITime
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(@RequestBody Map<String, Object> payload) {

        String jobDescription = (String) payload.get("jobDescription");
        Object resumeObj = payload.get("resume");

        if (jobDescription == null || jobDescription.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "jobDescription missing"));
        }

        if (resumeObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "resume missing"));
        }

        // Convert resume object to JSON string
        String resumeJson;
        try {
            resumeJson = new ObjectMapper().writeValueAsString(resumeObj);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "JSON serialization failed"));
        }

        // Call AI
        String aiOutput = groqService.analyze(jobDescription, resumeJson);

        // Convert AI output JSON string → Java Map → auto sent as JSON
        try {
            Object json = new ObjectMapper().readValue(aiOutput, Object.class);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "AI returned invalid JSON", "raw", aiOutput));
        }
    }

    @APITime
    @PostMapping("/create-report")
    public ResponseEntity<?> createReport(@RequestBody ResumeComparisonRequest payload) {
        Map<String, Object> oldResume = payload.getOldResume();
        Map<String, Object> newResume = payload.getNewResume();

        if (oldResume == null || newResume == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "both resumes are required"));
        }

        ResumeComparisonDiff diff = groqService.compareResumes(oldResume, newResume);

        return ResponseEntity.ok(diff);
    }

    @APITime
    @PostMapping("/enhanceResume")
    public ResponseEntity<?> enhanceResume(@RequestBody ResumeDTO dto) {
        try {
            // 1️⃣ Call the service to get raw AI JSON
            String aiJson = groqService.enhanceResumeSimple(dto);

            // 2️⃣ Parse AI JSON into a Map safely
            Map<String, Object> jsonResponse;
            try {
                jsonResponse = new ObjectMapper().readValue(aiJson, Map.class);
            } catch (JsonProcessingException e) {
                System.err.println("Failed to parse AI JSON. Raw content:");
                System.err.println(aiJson);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "AI returned invalid or truncated JSON."));
            }

            // 3️⃣ Return parsed JSON to frontend
            return ResponseEntity.ok(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "AI Resume enhancement failed: " + e.getMessage()));
        }
    }

    //
    // @PostMapping("/analyze-diff")
    // public ResponseEntity<Map<String, Object>> analyzeDiff(@RequestBody
    // DiffRequest req) {
    // Map<String, Object> diffJson = groqService.generateDiffJson(req);
    // return ResponseEntity.ok(diffJson);
    // }

}
