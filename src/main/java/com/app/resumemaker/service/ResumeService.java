package com.app.resumemaker.service;

import java.util.Optional;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.resumemaker.dto.*;
import com.app.resumemaker.model.*;
import com.app.resumemaker.respository.*;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;
    
    @Autowired
	private UserRepository userrepo;

    public void saveResume(ResumeDTO dto) {
        if (dto == null || dto.getDetails() == null || dto.getContact() == null) return;

        // ---------------- Create Resume ----------------
        Resume resume = new Resume();

        // Map basic info
        BasicInfoEntity basicInfo = new BasicInfoEntity();
        basicInfo.setName(dto.getDetails().getName());
        basicInfo.setTitle(dto.getDetails().getTitle());
        basicInfo.setSummary(dto.getDetails().getSummary());
        basicInfo.setSkills(dto.getDetails().getSkills());

        ContactEntity contact = new ContactEntity();
        contact.setPhone(dto.getContact().getPhone());
        contact.setEmail(dto.getContact().getEmail());
        contact.setGithub(dto.getContact().getGithub());
        contact.setLinkedin(dto.getContact().getLinkedin());
        contact.setLocation(dto.getContact().getLocation());

        basicInfo.setContact(contact);
        resume.setBasicInfo(basicInfo);

        resume.setExperienceSummary(dto.getDetails().getSummary());
        resume.setSkillsSummary(dto.getDetails().getSkills());

        // ---------------- Link and Save Children ----------------
        if (dto.getExperiences() != null) {
            dto.getExperiences().forEach(expDto -> {
                Experience exp = new Experience();
                exp.setPosition(expDto.getPosition());
                exp.setCompany(expDto.getCompany());
                exp.setDuration(expDto.getDuration());
                exp.setLocation(expDto.getLocation());
                resume.addExperience(exp);
            });
        }

        if (dto.getProjects() != null) {
            dto.getProjects().forEach(projDto -> {
                ProjectDetails proj = new ProjectDetails();
                proj.setProjectName(projDto.getName());
                proj.setTechStack(projDto.getTechnologies());
                proj.setDescription(projDto.getDescriptionAsText());
                proj.setLink(projDto.getLink());
                resume.addProject(proj);
            });
        }

        if (dto.getEducationList() != null) {
            dto.getEducationList().forEach(eduDto -> {
                Education edu = new Education();
                edu.setDegree(eduDto.getDegree());
                edu.setInstitution(eduDto.getInstitution());
                edu.setLocation(eduDto.getLocation());
                edu.setYear(eduDto.getYear());
                edu.setGpa(eduDto.getGpa());
                resume.addEducation(edu);
            });
        }

        if (dto.getCertifications() != null) {
            dto.getCertifications().forEach(certDto -> {
                Certification cert = new Certification();
                cert.setName(certDto.getName());
                resume.addCertification(cert);
            });
        }
        
        
       Optional<User> opt = userrepo.findById(dto.getUserId());
       resume.setUser(opt.get());
        // ---------------- Save Resume (cascade saves all children) ----------------
        resumeRepository.save(resume);
    }

}
