package com.strms.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strms.demo.Entites.Job;

public interface JobRepository extends JpaRepository<Job, String> {

}
