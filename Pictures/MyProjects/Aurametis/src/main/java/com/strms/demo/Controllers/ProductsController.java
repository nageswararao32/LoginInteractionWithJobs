package com.strms.demo.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Entites.Products;
import com.strms.demo.Repositories.ProductsRepository;

@RestController
@RequestMapping("/admin/products")

public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Products product) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (productsRepository.existsById(product.getId())) {
                response.put("success", false);
                response.put("message", "Product with this ID already exists");
                return ResponseEntity.ok(response);
            }

            Products saved = productsRepository.save(product);

            response.put("success", true);
            response.put("message", "Product added successfully");
            response.put("data", saved);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add product");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Products> products = productsRepository.findAll();
            response.put("success", true);
            response.put("data", products);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch products");
            return ResponseEntity.ok(response);
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Products> productOpt = productsRepository.findById(id);

        if (productOpt.isPresent()) {
            response.put("success", true);
            response.put("data", productOpt.get());
        } else {
            response.put("success", false);
            response.put("message", "Product not found");
        }

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @RequestBody Products updatedProduct) {

        Map<String, Object> response = new HashMap<>();

        Optional<Products> productOpt = productsRepository.findById(id);

        if (productOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Product not found");
            return ResponseEntity.ok(response);
        }

        Products product = productOpt.get();
        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setVersion(updatedProduct.getVersion());
        product.setStatus(updatedProduct.getStatus());
        product.setTags(updatedProduct.getTags());
        product.setVisible(updatedProduct.isVisible());

        productsRepository.save(product);

        response.put("success", true);
        response.put("message", "Product updated successfully");
        response.put("data", product);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/visibility/{id}")
    public ResponseEntity<?> updateVisibility(
            @PathVariable String id,
            @RequestParam boolean isVisible) {

        Map<String, Object> response = new HashMap<>();

        Optional<Products> productOpt = productsRepository.findById(id);

        if (productOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Product not found");
            return ResponseEntity.ok(response);
        }

        Products product = productOpt.get();
        product.setVisible(isVisible);
        productsRepository.save(product);

        response.put("success", true);
        response.put("message", "Visibility updated");
        response.put("isVisible", isVisible);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        if (!productsRepository.existsById(id)) {
            response.put("success", false);
            response.put("message", "Product not found");
            return ResponseEntity.ok(response);
        }

        productsRepository.deleteById(id);

        response.put("success", true);
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }
}
