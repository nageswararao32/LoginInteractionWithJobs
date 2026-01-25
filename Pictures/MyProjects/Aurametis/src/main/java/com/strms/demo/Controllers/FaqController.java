package com.strms.demo.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Entites.Faq;
import com.strms.demo.Repositories.FqaRepository;

@RestController
@RequestMapping("/admin/faq")

public class FaqController {

    @Autowired
    private FqaRepository faqRepository;

    // Get all FAQs
    @GetMapping
    public ResponseEntity<?> getAllFaqs() {
        try {
            List<Faq> faqs = faqRepository.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", faqs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addFaq(@RequestBody Faq faq) {
        try {
            Faq savedFaq = faqRepository.save(faq);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedFaq);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFaq(@PathVariable String id, @RequestBody Faq faqDetails) {
        try {
            Faq faq = faqRepository.findById(id).orElseThrow(() -> new RuntimeException("FAQ not found"));
            faq.setQuestion(faqDetails.getQuestion());
            faq.setAnswer(faqDetails.getAnswer());
            faq.setVisible(faqDetails.isVisible());
            Faq updatedFaq = faqRepository.save(faq);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedFaq);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFaq(@PathVariable String id) {
        try {
            faqRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "FAQ deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/visibility")
    public ResponseEntity<?> setVisibility(
            @PathVariable String id,
            @RequestParam boolean isVisible) {
        try {
            Faq faq = faqRepository.findById(id).orElseThrow(() -> new RuntimeException("FAQ not found"));
            faq.setVisible(!faq.isVisible());
            faqRepository.save(faq);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", faq);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
