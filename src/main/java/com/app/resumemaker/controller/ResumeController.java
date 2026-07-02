package com.app.resumemaker.controller;



import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.resumemaker.annotation.APITime;
import com.app.resumemaker.dto.BsaicInfoDTO;
import com.app.resumemaker.dto.ResumeDTO;
import com.app.resumemaker.model.BasicInfoEntity;
import com.app.resumemaker.model.Resume;
import com.app.resumemaker.service.GroqAIService;
import com.app.resumemaker.service.ResumeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.models.Paths;

@RestController
public class ResumeController {
	
	
	@Autowired
	ResumeService rs;
	
	@Autowired
	GroqAIService gs;

	
	@APITime
	@PostMapping("/saveall")
	public ResponseEntity<Map<String, Object>> saveAll(@RequestBody ResumeDTO dto) {

	    // Save the resume and get the generated ID
	    Long resumeId = rs.saveResume(dto); // make sure your service returns the generated resumeId

	    // Prepare response as JSON
	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "Resume saved successfully");
	    response.put("resumeId", resumeId);

	    return ResponseEntity.ok(response);
	}

	@APITime
	@PutMapping("/update/{resumeId}")
	public ResponseEntity<String> updateAll(@RequestBody ResumeDTO dto, @PathVariable Long resumeId) {
	    try {
	        rs.updateResume(resumeId, dto);
	        return ResponseEntity.ok("Resume updated successfully");
	    } catch (RuntimeException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error updating resume: " + ex.getMessage());
	    }
	}

	
    // --------------------------
    // Get all resumes for a user
    // --------------------------
    @APITime
    @GetMapping("/my-resumes/{userId}")
    public ResponseEntity<List<ResumeDTO>> getAllResumesByUser(@PathVariable Long userId) {
        List<ResumeDTO> resumes = rs.getAllResumesByUser(userId);
        return ResponseEntity.ok(resumes);
    }

    // --------------------------
    // Get single resume by ID (full details)
    // --------------------------
    @APITime
    @GetMapping("/my-resumes/getresume/{resumeId}")
    public ResponseEntity<ResumeDTO> getResumeById(@PathVariable Long resumeId) {
        ResumeDTO resume = rs.getResumeById(resumeId);
        if (resume == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(resume);
    }
    
    @APITime
    @DeleteMapping("/my-resumes/delete-resume/{resumeId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long resumeId) {
        String res = rs.deleteResume(resumeId);
        return ResponseEntity.ok(res);
    }

    
    
    @APITime
    @PostMapping("/uploadResume")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "File is empty"));
        }

        try {
            // 1️⃣ Extract text from PDF
            String text = rs.extractTextFromPdf(file);
            String normalizedText = text.replaceAll("\\s+", " ").trim();

            // 2️⃣ Generate AI JSON
            String aiJson = gs.generateResumeFromPdf(normalizedText);

         
            // 4️⃣ Parse AI JSON safely
            Map<String, Object> jsonResponse;
            try {
                jsonResponse = new ObjectMapper().readValue(aiJson, Map.class);
            } catch (JsonProcessingException e) {
                System.err.println("Failed to parse AI JSON. Raw content:");
                System.err.println(aiJson);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "AI returned invalid or truncated JSON."));
            }

            // 5️⃣ Return parsed JSON to frontend
            return ResponseEntity.ok().body(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "AI Resume generation failed: " + e.getMessage()));
        }
    }




   


	
	

	 
}
