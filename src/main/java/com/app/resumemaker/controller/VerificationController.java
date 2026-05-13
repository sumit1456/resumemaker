package com.app.resumemaker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.resumemaker.service.VerificationService;

@RestController
public class VerificationController {
	
	 @Autowired private VerificationService verificationService;

	    @GetMapping("/verify")
	    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
	        try {
	            verificationService.verifyToken(token);
	            return ResponseEntity.ok("✅ Email verified successfully!");
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
	        }
	    }

}
