package com.app.resumemaker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        System.out.println("AI RAW OUTPUT:");
        System.out.println(aiOutput);

        // Convert AI output JSON string → Java Map → auto sent as JSON
        try {
            Object json = new ObjectMapper().readValue(aiOutput, Object.class);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "AI returned invalid JSON", "raw", aiOutput)
            );
        }
    }
    
    

    
    
    

    @PostMapping("/create-report")
    public ResponseEntity<?> createReport(@RequestBody ResumeComparisonRequest payload) {
        System.out.println("Create report was called");
    	
    	Map<String, Object> oldResume = payload.getOldResume();
        Map<String, Object> newResume = payload.getNewResume();

        if (oldResume == null || newResume == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "both resumes are required"));
        }

        ResumeComparisonDiff diff = groqService.compareResumes(oldResume, newResume);

        System.out.println("Printing the output of create service");
        System.out.println(diff);
        return ResponseEntity.ok(diff); 
    }

    
    
    @PostMapping("/enhanceResume")
    public ResponseEntity<?> enhanceResume(@RequestBody ResumeDTO dto) {
    	System.out.println("Enhance Resume was called");
        try {
            ResumeDTO enhanced = groqService.enhanceResume(dto);
            System.out.println(enhanced);
            return ResponseEntity.ok(enhanced);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error enhancing resume: " + e.getMessage());
        }
    }
    
//    
//    @PostMapping("/analyze-diff")
//    public ResponseEntity<Map<String, Object>> analyzeDiff(@RequestBody DiffRequest req) {
//        Map<String, Object> diffJson = groqService.generateDiffJson(req);
//        return ResponseEntity.ok(diffJson);
//    }


}

