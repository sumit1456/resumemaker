package com.app.resumemaker.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resumemaker.model.VerificationToken;

public interface VerificationRepository extends JpaRepository<VerificationToken, Long> {


	VerificationToken save(VerificationToken verificationToken);

	Optional<VerificationToken> findByToken(String tokenValue);
	
	

	
	    
	void deleteByUserId(Long userId);
	
	VerificationToken findByUserId(Long userId);
	

	
	
}
