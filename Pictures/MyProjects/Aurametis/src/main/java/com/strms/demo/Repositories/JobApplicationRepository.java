package com.strms.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strms.demo.Entites.JobApplication;

public interface JobApplicationRepository extends  JpaRepository<JobApplication, String> {
    List<JobApplication> findByJobId(String jobId);
    List<JobApplication> findByStatus(String status);
    boolean existsByEmail(String email);
}
