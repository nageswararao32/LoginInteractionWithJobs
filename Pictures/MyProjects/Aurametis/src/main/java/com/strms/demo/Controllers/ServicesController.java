package com.strms.demo.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Entites.Services;
import com.strms.demo.Repositories.ServicesRepositories;

@RestController
@RequestMapping("/admin/services")

public class ServicesController {

    @Autowired
    private ServicesRepositories servicesRepositories;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addService(@RequestBody Services service) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (servicesRepositories.existsById(service.getId())) {
                response.put("success", false);
                response.put("message", "Service with this ID already exists");
                return ResponseEntity.ok(response);
            }

            Services saved = servicesRepositories.save(service);

            response.put("success", true);
            response.put("message", "Service added successfully");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add service");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllServices() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Services> services = servicesRepositories.findAll();
            response.put("success", true);
            response.put("data", services);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch services");
            return ResponseEntity.ok(response);
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Services> serviceOpt = servicesRepositories.findById(id);

        if (serviceOpt.isPresent()) {
            response.put("success", true);
            response.put("data", serviceOpt.get());
        } else {
            response.put("success", false);
            response.put("message", "Service not found");
        }

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateService(
            @PathVariable String id,
            @RequestBody Services updatedService) {

        Map<String, Object> response = new HashMap<>();

        Optional<Services> serviceOpt = servicesRepositories.findById(id);

        if (serviceOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Service not found");
            return ResponseEntity.ok(response);
        }

        Services service = serviceOpt.get();
        service.setTitle(updatedService.getTitle());
        service.setCategory(updatedService.getCategory());
        service.setDescription(updatedService.getDescription());
        service.setIcon(updatedService.getIcon());
        service.setTags(updatedService.getTags());
        service.setVisible(updatedService.isVisible());

        servicesRepositories.save(service);

        response.put("success", true);
        response.put("message", "Service updated successfully");
        response.put("data", service);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/visibility/{id}")
    public ResponseEntity<?> updateVisibility(
            @PathVariable String id,
            @RequestParam boolean isVisible) {

        Map<String, Object> response = new HashMap<>();

        Optional<Services> serviceOpt = servicesRepositories.findById(id);

        if (serviceOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Service not found");
            return ResponseEntity.ok(response);
        }

        Services service = serviceOpt.get();
        service.setVisible(isVisible);
        servicesRepositories.save(service);

        response.put("success", true);
        response.put("message", "Visibility updated");
        response.put("isVisible", isVisible);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteService(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        if (!servicesRepositories.existsById(id)) {
            response.put("success", false);
            response.put("message", "Service not found");
            return ResponseEntity.ok(response);
        }

        servicesRepositories.deleteById(id);

        response.put("success", true);
        response.put("message", "Service deleted successfully");
        return ResponseEntity.ok(response);
    }
}
