package com.app.resumemaker.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.resumemaker.dto.*;
import com.app.resumemaker.model.*;
import com.app.resumemaker.respository.ResumeRepository;
import com.app.resumemaker.respository.UserRepository;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userrepo;

    // --------------------------
    // Save or Create Resume
    // --------------------------
    public void saveResume(ResumeDTO dto) {
        if (dto == null || dto.getDetails() == null || dto.getContact() == null) return;

        Resume resume = new Resume();
        resume.setTemplateId(dto.getTemplateId());
        resume.setTitle(dto.getTitle());

        // Map Basic Info
        BasicInfoEntity basicInfo = new BasicInfoEntity();
        basicInfo.setName(dto.getDetails().getName());
        basicInfo.setTitle(dto.getDetails().getTitle());
        basicInfo.setSummary(dto.getDetails().getSummary());

        ContactEntity contact = new ContactEntity();
        contact.setPhone(dto.getContact().getPhone());
        contact.setEmail(dto.getContact().getEmail());
        contact.setGithub(dto.getContact().getGithub());
        contact.setLinkedin(dto.getContact().getLinkedin());
        contact.setLocation(dto.getContact().getLocation());

        basicInfo.setContact(contact);
        resume.setBasicInfo(basicInfo);

        resume.setExperienceSummary(dto.getDetails().getSummary());

        // Map Experiences
        if (dto.getExperiences() != null) {
            dto.getExperiences().forEach(expDto -> {
                Experience exp = new Experience();
                exp.setPosition(expDto.getPosition());
                exp.setCompany(expDto.getCompany());
                exp.setDuration(expDto.getDuration());
                exp.setLocation(expDto.getLocation());
                resume.getExperiences().add(exp);
                exp.setResume(resume);
            });
        }

        // Map Projects
        if (dto.getProjects() != null) {
            dto.getProjects().forEach(projDto -> {
                ProjectDetails proj = new ProjectDetails();
                proj.setProjectName(projDto.getName());
                proj.setTechStack(projDto.getTechnologies());
                proj.setDescription(projDto.getDescriptionAsText());
                proj.setLink(projDto.getLink());
                resume.getProjects().add(proj);
                proj.setResume(resume);
            });
        }

        // Map Education
        if (dto.getEducationList() != null) {
            dto.getEducationList().forEach(eduDto -> {
                Education edu = new Education();
                edu.setDegree(eduDto.getDegree());
                edu.setInstitution(eduDto.getInstitution());
                edu.setLocation(eduDto.getLocation());
                edu.setYear(eduDto.getYear());
                edu.setGpa(eduDto.getGpa());
                resume.getEducationList().add(edu);
                edu.setResume(resume);
            });
        }

        // Map Certifications
        if (dto.getCertifications() != null) {
            dto.getCertifications().forEach(certDto -> {
                Certification cert = new Certification();
                cert.setName(certDto.getName());
                resume.getCertifications().add(cert);
                cert.setResume(resume);
            });
        }

        // Map Skills
        if (dto.getSkills() != null) {
            dto.getSkills().forEach(skillDto -> {
                if (skillDto.getName() != null && !skillDto.getName().isEmpty()) {
                    Skill sk = new Skill();
                    sk.setName(skillDto.getName());
                    resume.getSkills().add(sk);
                    sk.setResume(resume);
                }
            });
        }

        // Set User
        Optional<User> opt = userrepo.findById(dto.getUserId());
        opt.ifPresent(resume::setUser);

        // Save Resume
        resumeRepository.save(resume);
    }
    
    
    
    public void updateResume(Long resumeId, ResumeDTO dto) {
        if (resumeId == null || dto == null) return;

        // Find existing resume
        Resume existingResume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found with ID: " + resumeId));

        // Update basic fields
        existingResume.setTitle(dto.getTitle());
        existingResume.setTemplateId(dto.getTemplateId());
        existingResume.setExperienceSummary(dto.getDetails() != null ? dto.getDetails().getSummary() : null);

        // Update basic info
        BasicInfoEntity basicInfo = existingResume.getBasicInfo();
        if (basicInfo == null) {
            basicInfo = new BasicInfoEntity();
            existingResume.setBasicInfo(basicInfo);
        }

        if (dto.getDetails() != null) {
            basicInfo.setName(dto.getDetails().getName());
            basicInfo.setTitle(dto.getDetails().getTitle());
            basicInfo.setSummary(dto.getDetails().getSummary());
        }

        // Update contact info
        ContactEntity contact = basicInfo.getContact();
        if (contact == null) {
            contact = new ContactEntity();
            basicInfo.setContact(contact);
        }
        if (dto.getContact() != null) {
            contact.setPhone(dto.getContact().getPhone());
            contact.setEmail(dto.getContact().getEmail());
            contact.setGithub(dto.getContact().getGithub());
            contact.setLinkedin(dto.getContact().getLinkedin());
            contact.setLocation(dto.getContact().getLocation());
        }

        // ✅ Clear and re-add collections (simplest approach)
        existingResume.getExperiences().clear();
        if (dto.getExperiences() != null) {
            dto.getExperiences().forEach(expDto -> {
                Experience exp = new Experience();
                exp.setPosition(expDto.getPosition());
                exp.setCompany(expDto.getCompany());
                exp.setDuration(expDto.getDuration());
                exp.setLocation(expDto.getLocation());
                exp.setResume(existingResume);
                existingResume.getExperiences().add(exp);
            });
        }

        existingResume.getProjects().clear();
        if (dto.getProjects() != null) {
            dto.getProjects().forEach(projDto -> {
                ProjectDetails proj = new ProjectDetails();
                proj.setProjectName(projDto.getName());
                proj.setTechStack(projDto.getTechnologies());
                proj.setDescription(projDto.getDescriptionAsText());
                proj.setLink(projDto.getLink());
                proj.setResume(existingResume);
                existingResume.getProjects().add(proj);
            });
        }

        existingResume.getEducationList().clear();
        if (dto.getEducationList() != null) {
            dto.getEducationList().forEach(eduDto -> {
                Education edu = new Education();
                edu.setDegree(eduDto.getDegree());
                edu.setInstitution(eduDto.getInstitution());
                edu.setLocation(eduDto.getLocation());
                edu.setYear(eduDto.getYear());
                edu.setGpa(eduDto.getGpa());
                edu.setResume(existingResume);
                existingResume.getEducationList().add(edu);
            });
        }

        existingResume.getCertifications().clear();
        if (dto.getCertifications() != null) {
            dto.getCertifications().forEach(certDto -> {
                Certification cert = new Certification();
                cert.setName(certDto.getName());
                cert.setResume(existingResume);
                existingResume.getCertifications().add(cert);
            });
        }

        existingResume.getSkills().clear();
        if (dto.getSkills() != null) {
            dto.getSkills().forEach(skillDto -> {
                if (skillDto.getName() != null && !skillDto.getName().isEmpty()) {
                    Skill sk = new Skill();
                    sk.setName(skillDto.getName());
                    sk.setResume(existingResume);
                    existingResume.getSkills().add(sk);
                }
            });
        }

        // ✅ Update User if needed
        if (dto.getUserId() != null) {
            Optional<User> opt = userrepo.findById(dto.getUserId());
            opt.ifPresent(existingResume::setUser);
        }

        // ✅ Save changes
        resumeRepository.save(existingResume);
    }


    // --------------------------
    // Fetch all resumes for a user (minimal info for MyResumes.jsx)
    // --------------------------
    public List<ResumeDTO> getAllResumesByUser(Long userId) {
        List<Resume> resumes = resumeRepository.findAllByUserId(userId);
        List<ResumeDTO> dtos = new ArrayList<>();

        for (Resume resume : resumes) {
            ResumeDTO dto = new ResumeDTO();
            dto.setTemplateId(resume.getTemplateId());
            dto.setUserId(resume.getUser() != null ? resume.getUser().getId() : null);
            dto.setTitle(resume.getTitle());
            dto.setId(resume.getId());

            // Minimal basic info for listing
            BsaicInfoDTO details = new BsaicInfoDTO();
            details.setName(resume.getBasicInfo() != null ? resume.getBasicInfo().getName() : null);
            dto.setDetails(details);

            dtos.add(dto);
        }

        return dtos;
    }

    // --------------------------
    // Fetch full resume by ID (for ResumeViewer.jsx)
    // --------------------------
    public ResumeDTO getResumeById(Long resumeId) {
        Resume resume = resumeRepository.findResumeWithAllDetails(resumeId);
        if (resume == null) return null;

        ResumeDTO dto = new ResumeDTO();
        dto.setTemplateId(resume.getTemplateId());
        dto.setUserId(resume.getUser() != null ? resume.getUser().getId() : null);
        dto.setTitle(resume.getTitle());
        dto.setId(resume.getId());

        // Map all related entities (Set -> List)
        dto.setDetails(mapBasicInfo(resume.getBasicInfo()));
        dto.setContact(mapContact(resume.getBasicInfo() != null ? resume.getBasicInfo().getContact() : null));
        dto.setExperiences(mapExperiences(resume.getExperiences()));
        dto.setProjects(mapProjects(resume.getProjects()));
        dto.setEducationList(mapEducation(resume.getEducationList()));
        dto.setCertifications(mapCertifications(resume.getCertifications()));
        dto.setSkills(mapSkills(resume.getSkills()));

        return dto;
    }

    // --------------------------
    // Mapping helpers (Set -> List)
    // --------------------------
    private BsaicInfoDTO mapBasicInfo(BasicInfoEntity basicInfo) {
        if (basicInfo == null) return null;
        BsaicInfoDTO dto = new BsaicInfoDTO();
        dto.setName(basicInfo.getName());
        dto.setTitle(basicInfo.getTitle());
        dto.setSummary(basicInfo.getSummary());
        return dto;
    }

    private ContactDTO mapContact(ContactEntity contact) {
        if (contact == null) return null;
        ContactDTO dto = new ContactDTO();
        dto.setEmail(contact.getEmail());
        dto.setPhone(contact.getPhone());
        dto.setGithub(contact.getGithub());
        dto.setLinkedin(contact.getLinkedin());
        dto.setLocation(contact.getLocation());
        return dto;
    }

    private List<ExperienceDTO> mapExperiences(Set<Experience> experiences) {
        List<ExperienceDTO> list = new ArrayList<>();
        if (experiences != null) {
            experiences.forEach(e -> {
                ExperienceDTO dto = new ExperienceDTO();
                dto.setPosition(e.getPosition());
                dto.setCompany(e.getCompany());
                dto.setDuration(e.getDuration());
                dto.setLocation(e.getLocation());
                list.add(dto);
            });
        }
        return list;
    }

    private List<ProjectDTO> mapProjects(Set<ProjectDetails> projects) {
        List<ProjectDTO> list = new ArrayList<>();
        if (projects != null) {
            projects.forEach(p -> {
                ProjectDTO dto = new ProjectDTO();
                dto.setName(p.getProjectName());
                dto.setTechnologies(p.getTechStack());
                dto.setDescriptionFromText(p.getDescription());
                dto.setLink(p.getLink());
                list.add(dto);
            });
        }
        return list;
    }

    private List<EducationDTO> mapEducation(Set<Education> educationList) {
        List<EducationDTO> list = new ArrayList<>();
        if (educationList != null) {
            educationList.forEach(e -> {
                EducationDTO dto = new EducationDTO();
                dto.setDegree(e.getDegree());
                dto.setInstitution(e.getInstitution());
                dto.setLocation(e.getLocation());
                dto.setYear(e.getYear());
                dto.setGpa(e.getGpa());
                list.add(dto);
            });
        }
        return list;
    }

    private List<CertificationsDTO> mapCertifications(Set<Certification> certifications) {
        List<CertificationsDTO> list = new ArrayList<>();
        if (certifications != null) {
            certifications.forEach(c -> {
                CertificationsDTO dto = new CertificationsDTO();
                dto.setName(c.getName());
                list.add(dto);
            });
        }
        return list;
    }

    private List<SkillDTO> mapSkills(Set<Skill> skills) {
        List<SkillDTO> list = new ArrayList<>();
        if (skills != null) {
            skills.forEach(s -> {
                SkillDTO dto = new SkillDTO();
                dto.setName(s.getName());
                list.add(dto);
            });
        }
        return list;
    }



	public String deleteResume(Long resumeId) {
	    Resume rs = resumeRepository.findById(resumeId).get();
	    resumeRepository.delete(rs);
		return "resume deleted successfully";
	    
	}
}
