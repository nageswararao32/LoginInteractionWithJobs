package com.strms.demo.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Entites.Headers;
import com.strms.demo.Repositories.HeadersRepository;

@RestController
@RequestMapping("/admin/headers")
public class HeadersController {

    @Autowired
    private HeadersRepository headersRepository;

    /**
     * Add a new header - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addHeader(@RequestBody Headers header) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (headersRepository.existsById(header.getId())) {
                response.put("success", false);
                response.put("message", "Header with this ID already exists");
                return ResponseEntity.ok(response);
            }

            // ✅ BUSINESS RULE:
            // nav-careers & nav-login → isExternal = true
            // all others → false
            if ("nav-careers".equals(header.getId()) ||
                    "nav-login".equals(header.getId())) {
                header.setExternal(true);
            } else {
                header.setExternal(false);
            }

            Headers savedHeader = headersRepository.save(header);

            response.put("success", true);
            response.put("message", "Header added successfully");
            response.put("data", savedHeader);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add header");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get all headers - PUBLIC ACCESS
     * Anyone can access this endpoint without authentication
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllHeaders() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Headers> headers = headersRepository.findAll();

            response.put("success", true);
            response.put("data", headers);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch headers");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Edit/Update a header - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editHeader(
            @PathVariable String id,
            @RequestBody Headers updatedHeader) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Headers> optionalHeader = headersRepository.findById(id);

            if (optionalHeader.isEmpty()) {
                response.put("success", false);
                response.put("message", "Header not found");
                return ResponseEntity.ok(response);
            }

            Headers existingHeader = optionalHeader.get();
            existingHeader.setLabel(updatedHeader.getLabel());
            existingHeader.setTo(updatedHeader.getTo());
            existingHeader.setVisible(updatedHeader.isVisible());

            Headers savedHeader = headersRepository.save(existingHeader);

            response.put("success", true);
            response.put("message", "Header updated successfully");
            response.put("data", savedHeader);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update header");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/visibility/{id}")
    public ResponseEntity<Map<String, Object>> updateVisibility(
            @PathVariable String id,
            @RequestParam("isVisible") boolean visibility) { // matches your query param

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Headers> optionalHeader = headersRepository.findById(id);

            if (optionalHeader.isEmpty()) {
                response.put("success", false);
                response.put("message", "Header not found");
                return ResponseEntity.ok(response);
            }

            Headers header = optionalHeader.get();
            header.setVisible(visibility);
            System.out.println("Updating header " + id + " to visible=" + visibility);

            headersRepository.save(header);

            response.put("success", true);
            response.put("message", "Visibility updated successfully");
            response.put("data", header);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update visibility");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a header - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteHeader(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!headersRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Header not found");
                return ResponseEntity.ok(response);
            }

            headersRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Header deleted successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete header");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}