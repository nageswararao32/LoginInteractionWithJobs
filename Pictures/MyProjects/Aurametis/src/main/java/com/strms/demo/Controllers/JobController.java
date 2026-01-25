package com.strms.demo.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Entites.Job;
import com.strms.demo.Repositories.JobRepository;

@RestController
@RequestMapping("/admin/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    /**
     * Get all jobs - PUBLIC ACCESS
     * Anyone can access this endpoint without authentication
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllJobs() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Job> jobs = jobRepository.findAll();

            response.put("success", true);
            response.put("data", jobs);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch jobs");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get job by ID - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getJobById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Job> job = jobRepository.findById(id);

            if (job.isEmpty()) {
                response.put("success", false);
                response.put("message", "Job not found");
                return ResponseEntity.ok(response);
            }

            response.put("success", true);
            response.put("data", job.get());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch job");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Add a new job - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addJob(@RequestBody Job job) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (jobRepository.existsById(job.getId())) {
                response.put("success", false);
                response.put("message", "Job with this ID already exists");
                return ResponseEntity.ok(response);
            }

            Job savedJob = jobRepository.save(job);

            response.put("success", true);
            response.put("message", "Job added successfully");
            response.put("data", savedJob);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add job");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Update a job - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateJob(
            @PathVariable String id,
            @RequestBody Job updatedJob) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Job> optionalJob = jobRepository.findById(id);

            if (optionalJob.isEmpty()) {
                response.put("success", false);
                response.put("message", "Job not found");
                return ResponseEntity.ok(response);
            }

            Job existingJob = optionalJob.get();
            existingJob.setTitle(updatedJob.getTitle());
            existingJob.setDepartment(updatedJob.getDepartment());
            existingJob.setLocation(updatedJob.getLocation());
            existingJob.setType(updatedJob.getType());
            existingJob.setDescription(updatedJob.getDescription());
            existingJob.setVisible(updatedJob.isVisible());

            Job savedJob = jobRepository.save(existingJob);

            response.put("success", true);
            response.put("message", "Job updated successfully");
            response.put("data", savedJob);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update job");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Update visibility of a job - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/visibility/{id}")
    public ResponseEntity<Map<String, Object>> updateVisibility(
            @PathVariable String id,
            @RequestParam boolean visibility) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Job> optionalJob = jobRepository.findById(id);

            if (optionalJob.isEmpty()) {
                response.put("success", false);
                response.put("message", "Job not found");
                return ResponseEntity.ok(response);
            }

            Job job = optionalJob.get();
            job.setVisible(visibility);

            jobRepository.save(job);

            response.put("success", true);
            response.put("message", "Visibility updated successfully");
            response.put("data", job);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update visibility");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a job - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!jobRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Job not found");
                return ResponseEntity.ok(response);
            }

            jobRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Job deleted successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete job");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}