package com.app.resumemaker.service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.resumemaker.dto.LoginRequestDTO;
import com.app.resumemaker.dto.SignupRequestDto;
import com.app.resumemaker.dto.SignupResponceDto;
import com.app.resumemaker.exception.InvalidCredentials;
import com.app.resumemaker.exception.UserExists;
import com.app.resumemaker.exception.UserNotFound;
import com.app.resumemaker.model.User;
import com.app.resumemaker.model.VerificationToken;
import com.app.resumemaker.respository.UserRepository;
import com.app.resumemaker.respository.VerificationRepository;
// ✅ Google API imports
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;


@Service
public class AuthService {

    @Autowired
    private UserRepository userrepo;

    @Autowired
    private PasswordEncoder passEncoder;
    
    
    @Autowired
    private VerificationRepository vr;
    
    
    @Autowired
    private BrevoService brevoService;

    // ✅ Manual registration
    public SignupResponceDto registerUser(SignupRequestDto user2) {
    	
        Optional<User> userExists = userrepo.findByEmail(user2.getEmail());
        
        if(!userExists.isEmpty() && userExists.get().isVerified()) {
        	throw new UserExists();
        }

        User user = new User();
        user.setUsername(user2.getName());
        user.setPassword(passEncoder.encode(user2.getPassword()));
        user.setEmail(user2.getEmail());

        userrepo.save(user);
        
       
        
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(user, token);
        vr.save(verificationToken);
        
        brevoService.sendVerificationEmail(user.getEmail(), token);
        
        
        
        return new SignupResponceDto("Registration successful", user.getId());
    }

    // ✅ Manual login
    public User authenticate(String email, String password) {
        Optional<User> data = userrepo.findByEmail(email);
        if (data.isEmpty()) {
            throw new UserNotFound("Please enter valid credentials");
        }

        User fromDatabase = data.get();
        boolean matches = passEncoder.matches(password, fromDatabase.getPassword());
        if (!matches) {
            throw new InvalidCredentials();
        }

        return fromDatabase;
    }

    // ✅ Google login
    public User loginWithGoogle(String token) throws Exception {
        // Step 1: Verify Google token
        
    	GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
    	        GoogleNetHttpTransport.newTrustedTransport(),
    	        GsonFactory.getDefaultInstance() // ✅ use GsonFactory instead of JacksonFactory
    	)
    	.setAudience(Collections.singletonList("702821068415-um4cbj2o2m9rog3t1gdlqhcbudhph6p9.apps.googleusercontent.com")) // your Google client ID here
    	.build();


        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            throw new IllegalArgumentException("Invalid Google ID token");
        }

        // Step 2: Extract user info from token payload
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        // Step 3: Check if user already exists
        Optional<User> optionalUser = userrepo.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            // Step 4: Create new user
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setPassword(null); // No password for Google users
            userrepo.save(user);
        }

        // Step 5: Return user
        return user;
    }
}
