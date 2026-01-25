package com.strms.demo.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Entites.Projects;
import com.strms.demo.Repositories.ProjectsRepository;

@RestController
@RequestMapping("/admin/projects")
//@PreAuthorize("hasRole('ADMIN')")
public class ProjectsController {

    @Autowired
    private ProjectsRepository projectsRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addProject(@RequestBody Projects project) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (projectsRepository.existsById(project.getId())) {
                response.put("success", false);
                response.put("message", "Project with this ID already exists");
                return ResponseEntity.ok(response);
            }

            Projects saved = projectsRepository.save(project);

            response.put("success", true);
            response.put("message", "Project added successfully");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add project");
            return ResponseEntity.ok(response);
        }
    }

    /* ================= GET ALL PROJECTS ================= */
    @GetMapping("/all")
    public ResponseEntity<?> getAllProjects() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Projects> projects = projectsRepository.findAll();
            response.put("success", true);
            response.put("data", projects);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch projects");
            return ResponseEntity.ok(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Projects> projectOpt = projectsRepository.findById(id);

        if (projectOpt.isPresent()) {
            response.put("success", true);
            response.put("data", projectOpt.get());
        } else {
            response.put("success", false);
            response.put("message", "Project not found");
        }

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable String id,
            @RequestBody Projects updatedProject) {

        Map<String, Object> response = new HashMap<>();

        Optional<Projects> projectOpt = projectsRepository.findById(id);

        if (projectOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Project not found");
            return ResponseEntity.ok(response);
        }

        Projects project = projectOpt.get();
        project.setTitle(updatedProject.getTitle());
        project.setCategory(updatedProject.getCategory());
        project.setDescription(updatedProject.getDescription());
        project.setTechStack(updatedProject.getTechStack());
        project.setVisible(updatedProject.isVisible());

        projectsRepository.save(project);

        response.put("success", true);
        response.put("message", "Project updated successfully");
        response.put("data", project);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/visibility/{id}")
    public ResponseEntity<?> updateVisibility(
            @PathVariable String id,
            @RequestParam boolean isVisible) {

        Map<String, Object> response = new HashMap<>();

        Optional<Projects> projectOpt = projectsRepository.findById(id);

        if (projectOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Project not found");
            return ResponseEntity.ok(response);
        }

        Projects project = projectOpt.get();
        project.setVisible(isVisible);
        projectsRepository.save(project);

        response.put("success", true);
        response.put("message", "Visibility updated");
        response.put("isVisible", isVisible);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        if (!projectsRepository.existsById(id)) {
            response.put("success", false);
            response.put("message", "Project not found");
            return ResponseEntity.ok(response);
        }

        projectsRepository.deleteById(id);

        response.put("success", true);
        response.put("message", "Project deleted successfully");
        return ResponseEntity.ok(response);
    }
}
