package com.app.resumemaker.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.app.resumemaker.dto.ResumeDTO;
import com.app.resumemaker.model.Resume;



@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {

	void save(ResumeDTO resume);


	

}
