package com.app.resumemaker.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDto dto) {
        try {
            authService.registerUser(dto);
            return ResponseEntity.ok(Map.of(
            	    "success", true,
            	    "message", "Registered Successfully"
            	));

        } catch (Exception e) {
            e.printStackTrace(); // shows exact error in your Spring console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Long> loginRequest(@RequestBody LoginRequestDTO dto) {
        User user = authService.authenticate(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(user.getId()); // ✅ Return only userId
    }

    @PostMapping("/google-login")
    public ResponseEntity<Long> googleLogin(@RequestBody GoogleLoginRequest request) {
        System.out.println("📩 Google login request received");

        try {
            User user = authService.loginWithGoogle(request.getToken());
            return ResponseEntity.ok(user.getId()); // ✅ Return only userId
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }



      
}