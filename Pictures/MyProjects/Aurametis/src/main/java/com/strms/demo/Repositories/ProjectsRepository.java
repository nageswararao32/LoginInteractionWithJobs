package com.strms.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strms.demo.Entites.Projects;

public interface ProjectsRepository extends JpaRepository<Projects, String>{

}
