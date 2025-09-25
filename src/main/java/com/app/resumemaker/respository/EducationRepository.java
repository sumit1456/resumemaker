package com.app.resumemaker.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resumemaker.model.Education;

public interface EducationRepository extends JpaRepository<Education, Long> {

}
