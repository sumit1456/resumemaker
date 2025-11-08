package com.app.resumemaker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.resumemaker.dto.GoogleLoginRequest;
import com.app.resumemaker.dto.LoginRequestDTO;
import com.app.resumemaker.dto.SignupRequestDto;
import com.app.resumemaker.exception.InvalidCredentials;
import com.app.resumemaker.model.Resume;
import com.app.resumemaker.model.User;
import com.app.resumemaker.respository.UserRepository;
import com.app.resumemaker.service.AuthService;


//CONTROLLER FOR TESTINHG
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthService authService;

    // Test endpoint to check API is up
    @GetMapping
    ("/ping")
    public String ping() {
        return "Server is running!";
    }

    // Simple sign-up (creates a new user record)
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignupRequestDto dto) {
        authService.registerUser(dto);          // pass the DTO you received
        return ResponseEntity.ok("Registered Successfully");
    }

    
    @PostMapping("/login")
    public User loginRequest(@RequestBody LoginRequestDTO dto){
    	
    	return authService.authenticate(dto.getEmail(), dto.getPassword());
    }
    

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
    	System.out.println("The request was made");
        try {
            User user = authService.loginWithGoogle(request.getToken());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Google login failed: " + e.getMessage());
        }
    }

      
}