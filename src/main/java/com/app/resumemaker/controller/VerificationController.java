package com.app.resumemaker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.resumemaker.service.VerificationService;

@RestController
public class VerificationController {

    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        String tokenPrefix = token == null || token.length() < 8 ? "short-token" : token.substring(0, 8);
        logger.info("Email verification request received. tokenPrefix={}, tokenLength={}",
                tokenPrefix,
                token == null ? 0 : token.length());

        try {
            verificationService.verifyToken(token);
            logger.info("Email verification succeeded. tokenPrefix={}", tokenPrefix);
            return ResponseEntity.ok("Email verified successfully!");
        } catch (Exception e) {
            logger.warn("Email verification failed. tokenPrefix={}, reason={}", tokenPrefix, e.getMessage());
            return ResponseEntity.badRequest().body("Verification failed: " + e.getMessage());
        }
    }
}
