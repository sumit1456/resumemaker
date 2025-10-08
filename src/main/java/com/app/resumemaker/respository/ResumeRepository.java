package com.app.resumemaker.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.resumemaker.model.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // Fetch all resumes for a specific user (for MyResumes.jsx)
    List<Resume> findAllByUserId(Long userId);

    @Query("""
    	    SELECT r FROM Resume r
    	    LEFT JOIN FETCH r.basicInfo
    	    LEFT JOIN FETCH r.educationList
    	    LEFT JOIN FETCH r.experiences
    	    LEFT JOIN FETCH r.projects
    	    LEFT JOIN FETCH r.skills
    	    LEFT JOIN FETCH r.certifications
    	    WHERE r.id = :resumeId
    	""")
    	Resume findResumeWithAllDetails(@Param("resumeId") Long resumeId);

}
