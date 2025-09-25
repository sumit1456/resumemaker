package com.app.resumemaker.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resumemaker.model.Certification;
import com.app.resumemaker.model.Education;

public interface CertificationRepository extends JpaRepository<Education, Long> {

	void save(Certification cert);

}
