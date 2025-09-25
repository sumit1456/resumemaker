package com.app.resumemaker.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.resumemaker.model.BasicInfoEntity;
import com.app.resumemaker.model.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

	void save(BasicInfoEntity basicInfo);
	
	
	
		

}
