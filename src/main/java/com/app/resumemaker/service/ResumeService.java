package com.app.resumemaker.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.app.resumemaker.dto.ProjectDetailsDTO;
import com.app.resumemaker.dto.ResumeDTO;
import com.app.resumemaker.exception.UserNotFound;
import com.app.resumemaker.model.ProjectDetails;
import com.app.resumemaker.model.Resume;
import com.app.resumemaker.model.User;
import com.app.resumemaker.respository.ProjectDetailsRepository;
import com.app.resumemaker.respository.ResumeRepository;
import com.app.resumemaker.respository.UserRepository;

@Service
public class ResumeService {
	
	
	@Autowired
	private ResumeRepository resumeRepository;
	
	
	@Autowired
	private ProjectDetailsRepository pdr;
	
	@Autowired
	private UserRepository userRepo;
	public Resume saveResumeDetails(ResumeDTO resume) {
	    System.out.println(resume);

	    // Find the user by email
	    Optional<User> userOpt = userRepo.findByEmail(resume.getEmail());
	    if (userOpt.isEmpty()) {
	        throw new UserNotFound("User does not exist. Please login first.");
	    }
	    User user = userOpt.get();

	    System.out.println("Resume is being checked");

	    // Map DTO to entity
	    Resume rs = new Resume();
	    rs.setName(resume.getName());
	    rs.setEmail(resume.getEmail());
	    rs.setPhone(resume.getPhone());
	    rs.setUniversity(resume.getUniversity());
	    rs.setPassoutYear(resume.getPassoutYear());
	    rs.setStream(resume.getStream());
	    rs.setExperience(resume.getExperience());
	    rs.setSkills(resume.getSkills());

	    // Associate resume with the user
	    rs.setUser(user);

	    // Save the resume
	    resumeRepository.save(rs);

	    return rs;
	}
	
	public ProjectDetails saveProjectDetails(ProjectDetailsDTO dto, long id) {
		// TODO Auto-generated method stub
		ProjectDetails pd = new ProjectDetails();
		pd.setId(dto.getId());
		pd.setProjectName(dto.getProjectName());
		pd.setTechStack(dto.getTechStack());
	    Optional<Resume> resume = resumeRepository.findById(id);
	    pd.setResume(resume.get());
	    pdr.save(pd);
		return pd;
	}

}
