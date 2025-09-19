package com.app.resumemaker.model;



import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
	public String toString() {
		return "Resume [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + ", university="
				+ university + ", passoutYear=" + passoutYear + ", stream=" + stream + ", experience=" + experience
				+ ", skills=" + skills + "]";
	}
	private String name;
    private String email;
    private String phone;

    private String university;
    private String passoutYear; // or Integer if you prefer
    private String stream;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ProjectDetails> projects = new ArrayList<>();

    
   
    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String skills;

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getPassoutYear() { return passoutYear; }
    public void setPassoutYear(String passoutYear) { this.passoutYear = passoutYear; }

    public String getStream() { return stream; }
    public void setStream(String stream) { this.stream = stream; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    
    public void setUser(User user) {
        this.user = user;
    }
}
