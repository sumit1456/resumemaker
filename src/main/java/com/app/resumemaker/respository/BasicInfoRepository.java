package com.app.resumemaker.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resumemaker.model.BasicInfoEntity;

public interface BasicInfoRepository extends JpaRepository<BasicInfoEntity, Long> {

}
