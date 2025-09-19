package com.app.resumemaker.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.app.resumemaker.model.ProjectDetails;



@Service
public interface ProjectDetailsRepository extends JpaRepository<ProjectDetails, Long> {

}
