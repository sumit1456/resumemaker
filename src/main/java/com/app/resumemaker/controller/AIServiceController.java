package com.app.resumemaker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

        String resumeJson;
        try {
            // Serialize only the resume object, not full payload
            resumeJson = new ObjectMapper().writeValueAsString(resumeObj);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "JSON serialization failed"));
        }

        // MAIN CALL
        String result = groqService.analyze(jobDescription, resumeJson);

        return ResponseEntity.ok(Map.of("result", result));
    }
}

