package com.strms.demo.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Entites.Stats;
import com.strms.demo.Repositories.StatsRepository;

@RestController
@RequestMapping("/admin/stats")
public class StatsController {

    @Autowired
    private StatsRepository statsRepository;

    /**
     * Add new stats - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addStats(@RequestBody Stats stats) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (statsRepository.existsById(stats.getId())) {
                response.put("success", false);
                response.put("message", "Stats with this ID already exists");
                return ResponseEntity.ok(response);
            }

            Stats savedStats = statsRepository.save(stats);

            response.put("success", true);
            response.put("message", "Stats added successfully");
            response.put("data", savedStats);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add stats");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get all stats - PUBLIC ACCESS
     * Anyone can access this endpoint without authentication
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStats() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Stats> statsList = statsRepository.findAll();

            response.put("success", true);
            response.put("data", statsList);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch stats");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Edit/Update stats - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editStats(
            @PathVariable String id,
            @RequestBody Stats updatedStats) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Stats> optionalStats = statsRepository.findById(id);

            if (optionalStats.isEmpty()) {
                response.put("success", false);
                response.put("message", "Stats not found");
                return ResponseEntity.ok(response);
            }

            Stats existingStats = optionalStats.get();
            existingStats.setProjectCount(updatedStats.getProjectCount());
            existingStats.setRetentionRate(updatedStats.getRetentionRate());

            Stats savedStats = statsRepository.save(existingStats);

            response.put("success", true);
            response.put("message", "Stats updated successfully");
            response.put("data", savedStats);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update stats");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    
    /**
     * Delete stats - ADMIN ONLY
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteStats(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!statsRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Stats not found");
                return ResponseEntity.ok(response);
            }

            statsRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Stats deleted successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete stats");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}