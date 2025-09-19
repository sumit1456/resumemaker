package com.app.resumemaker.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.app.resumemaker.dto.ResumeDTO;
import com.app.resumemaker.model.ProjectDetails;
import com.app.resumemaker.model.Resume;



@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {

	void save(ResumeDTO resume);
    
	Optional<Resume> findById(long id);

	void save(ProjectDetails pd);

	

}
