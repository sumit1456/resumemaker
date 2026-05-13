package com.app.resumemaker.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resumemaker.model.Experience;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

}
