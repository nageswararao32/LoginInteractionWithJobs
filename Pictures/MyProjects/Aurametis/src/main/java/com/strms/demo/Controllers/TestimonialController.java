package com.strms.demo.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Entites.Testimonial;
import com.strms.demo.Repositories.TestimonialRepository;

@RestController
@RequestMapping("/admin/testimonials")
public class TestimonialController {

    @Autowired
    private TestimonialRepository testimonialRepository;

    /**
     * Get all testimonials - PUBLIC ACCESS
     * Anyone can access this endpoint without authentication
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllTestimonials() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Testimonial> testimonials = testimonialRepository.findAll();

            response.put("success", true);
            response.put("data", testimonials);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch testimonials");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get testimonial by ID - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTestimonialById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Testimonial> testimonial = testimonialRepository.findById(id);

            if (testimonial.isEmpty()) {
                response.put("success", false);
                response.put("message", "Testimonial not found");
                return ResponseEntity.ok(response);
            }

            response.put("success", true);
            response.put("data", testimonial.get());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch testimonial");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Add a new testimonial - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addTestimonial(@RequestBody Testimonial testimonial) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (testimonialRepository.existsById(testimonial.getId())) {
                response.put("success", false);
                response.put("message", "Testimonial with this ID already exists");
                return ResponseEntity.ok(response);
            }

            Testimonial savedTestimonial = testimonialRepository.save(testimonial);

            response.put("success", true);
            response.put("message", "Testimonial added successfully");
            response.put("data", savedTestimonial);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add testimonial");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Update a testimonial - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateTestimonial(
            @PathVariable String id,
            @RequestBody Testimonial updatedTestimonial) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Testimonial> optionalTestimonial = testimonialRepository.findById(id);

            if (optionalTestimonial.isEmpty()) {
                response.put("success", false);
                response.put("message", "Testimonial not found");
                return ResponseEntity.ok(response);
            }

            Testimonial existingTestimonial = optionalTestimonial.get();
            existingTestimonial.setName(updatedTestimonial.getName());
            existingTestimonial.setRole(updatedTestimonial.getRole());
            existingTestimonial.setCompany(updatedTestimonial.getCompany());
            existingTestimonial.setContent(updatedTestimonial.getContent());
            existingTestimonial.setAvatar(updatedTestimonial.getAvatar());
            existingTestimonial.setVisible(updatedTestimonial.isVisible());

            Testimonial savedTestimonial = testimonialRepository.save(existingTestimonial);

            response.put("success", true);
            response.put("message", "Testimonial updated successfully");
            response.put("data", savedTestimonial);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update testimonial");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Update visibility of a testimonial - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/visibility/{id}")
    public ResponseEntity<Map<String, Object>> updateVisibility(
            @PathVariable String id,
            @RequestParam boolean visibility) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Testimonial> optionalTestimonial = testimonialRepository.findById(id);

            if (optionalTestimonial.isEmpty()) {
                response.put("success", false);
                response.put("message", "Testimonial not found");
                return ResponseEntity.ok(response);
            }

            Testimonial testimonial = optionalTestimonial.get();
            testimonial.setVisible(visibility);

            testimonialRepository.save(testimonial);

            response.put("success", true);
            response.put("message", "Visibility updated successfully");
            response.put("data", testimonial);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update visibility");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a testimonial - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteTestimonial(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!testimonialRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Testimonial not found");
                return ResponseEntity.ok(response);
            }

            testimonialRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Testimonial deleted successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete testimonial");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}