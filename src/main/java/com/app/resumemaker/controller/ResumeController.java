package com.app.resumemaker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.resumemaker.dto.BsaicInfoDTO;
import com.app.resumemaker.dto.ResumeDTO;
import com.app.resumemaker.model.BasicInfoEntity;
import com.app.resumemaker.model.Resume;
import com.app.resumemaker.service.ResumeService;

@RestController
public class ResumeController {
	
	
	@Autowired
	ResumeService rs;

	
	@PostMapping("saveall")
	public ResponseEntity<String> saveAll(@RequestBody ResumeDTO dto) {
		System.out.println("Request was made");
	    rs.saveResume(dto);
	    return ResponseEntity.ok("Resume saved successfully");
	}
	
    // --------------------------
    // Get all resumes for a user
    // --------------------------
    @GetMapping("/my-resumes/{userId}")
    public ResponseEntity<List<ResumeDTO>> getAllResumesByUser(@PathVariable Long userId) {
        List<ResumeDTO> resumes = rs.getAllResumesByUser(userId);
        return ResponseEntity.ok(resumes);
    }

    // --------------------------
    // Get single resume by ID (full details)
    // --------------------------
    @GetMapping("/my-resumes/getresume/{resumeId}")
    public ResponseEntity<ResumeDTO> getResumeById(@PathVariable Long resumeId) {
        System.out.println("TGhe rrquest was made for viewing single resume");
        ResumeDTO resume = rs.getResumeById(resumeId);
        if (resume == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resume);
    }
   


	
	

	 
}
