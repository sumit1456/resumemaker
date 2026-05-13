package com.app.resumemaker.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resumemaker.model.ContactEntity;

public interface ContactRepository extends JpaRepository<ContactEntity, Long> {

}
