package com.app.resumemaker.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.resumemaker.model.User;
import com.app.resumemaker.model.VerificationToken;
import com.app.resumemaker.respository.UserRepository;
import com.app.resumemaker.respository.VerificationRepository;

@Service
public class VerificationService {
	
	

    @Autowired private VerificationRepository tokenRepo;
    @Autowired private UserRepository userRepo;

    public VerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(user, token);
        return tokenRepo.save(verificationToken);
    }

    public boolean verifyToken(String tokenValue) {
        VerificationToken token = tokenRepo.findByToken(tokenValue)
            .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = token.getUser();
        user.setVerified(true);
        userRepo.save(user);

      

        return true;
    }	

}
