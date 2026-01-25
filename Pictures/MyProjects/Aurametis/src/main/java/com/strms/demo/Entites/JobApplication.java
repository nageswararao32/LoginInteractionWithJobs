package com.strms.demo.Entites;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String jobId;
    
    @Column(nullable = false)
    private String jobTitle;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private Double yearsOfExperience;
    
    @Column(nullable = false)
    private String linkedinUrl;
    
    @Column
    private String portfolioUrl;
    
    @Column(nullable = false)
    private String resumeFileName;
    
    @Column(nullable = false)
    private String resumeFileType; // e.g., "application/pdf", "application/msword"
    
    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] resumeData; // Stores the actual file content
    
    @Column(nullable = false)
    private LocalDateTime appliedAt;
    
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, REVIEWED, SHORTLISTED, REJECTED
    
    // Constructors
    public JobApplication() {
        this.appliedAt = LocalDateTime.now();
    }
    
    public JobApplication(String jobId, String jobTitle, String firstName, String lastName, 
                         String email, Double yearsOfExperience, String linkedinUrl, 
                         String portfolioUrl, String resumeFileName, String resumeFileType, 
                         byte[] resumeData) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.yearsOfExperience = yearsOfExperience;
        this.linkedinUrl = linkedinUrl;
        this.portfolioUrl = portfolioUrl;
        this.resumeFileName = resumeFileName;
        this.resumeFileType = resumeFileType;
        this.resumeData = resumeData;
        this.appliedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Double getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(Double yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public String getLinkedinUrl() {
        return linkedinUrl;
    }
    
    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }
    
    public String getPortfolioUrl() {
        return portfolioUrl;
    }
    
    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
    
    public String getResumeFileName() {
        return resumeFileName;
    }
    
    public void setResumeFileName(String resumeFileName) {
        this.resumeFileName = resumeFileName;
    }
    
    public String getResumeFileType() {
        return resumeFileType;
    }
    
    public void setResumeFileType(String resumeFileType) {
        this.resumeFileType = resumeFileType;
    }
    
    public byte[] getResumeData() {
        return resumeData;
    }
    
    public void setResumeData(byte[] resumeData) {
        this.resumeData = resumeData;
    }
    
    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
    
    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
