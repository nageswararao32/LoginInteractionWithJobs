package com.strms.demo.Controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.strms.demo.Entites.JobApplication;
import com.strms.demo.Repositories.JobApplicationRepository;
import com.strms.demo.Services.EmailService;

@RestController
@RequestMapping("/api/applications")

public class JobApplicationController {

    @Autowired
    private JobApplicationRepository applicationRepository;
    
    @Autowired
    private EmailService emailService;

    /**
     * Submit a new job application - PUBLIC ACCESS
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitApplication(
            @RequestParam("jobId") String jobId,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("yearsOfExperience") Double yearsOfExperience,
            @RequestParam("linkedinUrl") String linkedinUrl,
            @RequestParam(value = "portfolioUrl", required = false) String portfolioUrl,
            @RequestParam("resume") MultipartFile resume) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if user already applied
            if (applicationRepository.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "You have already submitted an application");
                return ResponseEntity.ok(response);
            }

            // Validate file
            if (resume.isEmpty()) {
                response.put("success", false);
                response.put("message", "Resume file is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate file size (5MB limit)
            if (resume.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "File size must be less than 5MB");
                return ResponseEntity.badRequest().body(response);
            }

            // Get file data
            byte[] resumeData = resume.getBytes();
            String resumeFileName = resume.getOriginalFilename();
            String resumeFileType = resume.getContentType();

            // Create application entity with resume data stored as BLOB
            JobApplication application = new JobApplication(
                jobId, jobTitle, firstName, lastName, email, 
                yearsOfExperience, linkedinUrl, portfolioUrl, 
                resumeFileName, resumeFileType, resumeData
            );

            JobApplication savedApplication = applicationRepository.save(application);

            // Send emails with resume attachment to admin
            try {
                emailService.sendApplicationNotificationToAdmin(
                    firstName + " " + lastName, jobTitle, email, linkedinUrl, 
                    yearsOfExperience, resumeData, resumeFileName
                );
                emailService.sendConfirmationToApplicant(email, firstName, jobTitle);
            } catch (Exception emailEx) {
                System.err.println("Failed to send email: " + emailEx.getMessage());
                // Continue even if email fails
            }

            // Don't include resume data in response (too large)
            Map<String, Object> applicationData = new HashMap<>();
            applicationData.put("id", savedApplication.getId());
            applicationData.put("jobTitle", savedApplication.getJobTitle());
            applicationData.put("firstName", savedApplication.getFirstName());
            applicationData.put("lastName", savedApplication.getLastName());
            applicationData.put("email", savedApplication.getEmail());
            applicationData.put("appliedAt", savedApplication.getAppliedAt());
            applicationData.put("status", savedApplication.getStatus());

            response.put("success", true);
            response.put("message", "Application submitted successfully");
            response.put("data", applicationData);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to read resume file");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to submit application");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get all applications - ADMIN ONLY (without resume data)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllApplications() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<JobApplication> applications = applicationRepository.findAll();
            
            // Remove resume data from response to reduce payload size
            applications.forEach(app -> app.setResumeData(null));

            response.put("success", true);
            response.put("data", applications);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch applications");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Download resume - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/resume/{id}")
    public ResponseEntity<ByteArrayResource> downloadResume(@PathVariable String id) {
        try {
            JobApplication application = applicationRepository.findById(id).orElse(null);

            if (application == null) {
                return ResponseEntity.notFound().build();
            }

            ByteArrayResource resource = new ByteArrayResource(application.getResumeData());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + application.getResumeFileName() + "\"")
                    .contentType(MediaType.parseMediaType(application.getResumeFileType()))
                    .contentLength(application.getResumeData().length)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get applications by job ID - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/job/{jobId}")
    public ResponseEntity<Map<String, Object>> getApplicationsByJob(@PathVariable String jobId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<JobApplication> applications = applicationRepository.findByJobId(jobId);
            
            // Remove resume data from response
            applications.forEach(app -> app.setResumeData(null));

            response.put("success", true);
            response.put("data", applications);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch applications");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Update application status - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            JobApplication application = applicationRepository.findById(id).orElse(null);

            if (application == null) {
                response.put("success", false);
                response.put("message", "Application not found");
                return ResponseEntity.ok(response);
            }

            application.setStatus(status);
            applicationRepository.save(application);
            
            // Remove resume data from response
            application.setResumeData(null);

            response.put("success", true);
            response.put("message", "Status updated successfully");
            response.put("data", application);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update status");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Delete application - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteApplication(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!applicationRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Application not found");
                return ResponseEntity.ok(response);
            }

            applicationRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Application deleted successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete application");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
