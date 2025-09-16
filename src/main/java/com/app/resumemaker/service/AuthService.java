package com.app.resumemaker.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.resumemaker.dto.LoginRequestDTO;
import com.app.resumemaker.dto.SignupRequestDto;
import com.app.resumemaker.dto.SignupResponceDto;
import com.app.resumemaker.exception.InvalidCredentials;
import com.app.resumemaker.exception.UserExists;
import com.app.resumemaker.model.User;
import com.app.resumemaker.respository.UserRepository;

@Service
public class AuthService {
	
	
	@Autowired
	private UserRepository userrepo;
	
	@Autowired
	private PasswordEncoder passEncoder;
	
	
	public SignupResponceDto registerUser(SignupRequestDto user2) {
		
		if(userrepo.existsByEmail(user2.getEmail())) {
			throw new UserExists();
		}
		
		User user = new User();
		user.setUsername(user2.getName());
		user.setPassword(passEncoder.encode(user2.getPassword()));
		user.setEmail(user2.getEmail());
		
		userrepo.save(user);
		return new SignupResponceDto("Registration sucessful", user.getId());
		
		
	}
	
	public boolean authenticate(String email, String password) {
		Optional<User> data = userrepo.findByEmail(email);
		if(data.isEmpty()) {
			throw new InvalidCredentials();
		}
		User fromDatabase = data.get();
		return passEncoder.matches(password, fromDatabase.getPassword());
	}

	
}
