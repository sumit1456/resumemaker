package com.app.resumemaker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.resumemaker.dto.ProjectDetailsDTO;
import com.app.resumemaker.dto.ResumeDTO;
import com.app.resumemaker.model.ProjectDetails;
import com.app.resumemaker.model.Resume;
import com.app.resumemaker.service.ResumeService;

@RestController
public class ResumeController {
	
	
	@Autowired
	ResumeService rs;
	
	 @PostMapping("/createresume")
	    public Resume createResume(@RequestBody ResumeDTO resume){
		   return rs.saveResumeDetails(resume);
	   
	  }
	 
	 @PostMapping("/{resumeId}/saveprojects")
	 	public ProjectDetails saveProjects(@RequestBody ProjectDetailsDTO dto, @PathVariable long resumeId) {
			return rs.saveProjectDetails(dto,  resumeId);
	 }

}
