package com.strms.demo.Controllers;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.strms.demo.Entites.AdminUser;
import com.strms.demo.Entites.Headers;
import com.strms.demo.Repositories.AdminUserRepository;
import com.strms.demo.Repositories.HeadersRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final AdminUserRepository adminUserRepository;
    private final HeadersRepository headersRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(AdminUserRepository adminUserRepository, 
                          HeadersRepository headersRepository,
                          PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.headersRepository = headersRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) {
        log.info("Starting database initialization...");
        
        initializeAdminUsers();
        initializeHeaders();
        
        log.info("Database initialization completed");
    }
    
    private void initializeAdminUsers() {
        // Admin 1: sseswar2218@gmail.com
        if (!adminUserRepository.existsByEmail("sseswar2218@gmail.com")) {
            AdminUser admin1 = AdminUser.builder()
                .email("sseswar2218@gmail.com")
                .name("Naresh G")
                .password(passwordEncoder.encode("admin123"))
                .role(AdminUser.Role.ADMIN)
                .isActive(true)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            adminUserRepository.save(admin1);
            log.info("Created admin user: {}", admin1.getEmail());
        } else {
            log.info("Admin user already exists: sseswar2218@gmail.com");
        }
        
        // Admin 2: gurijalanag2146@gmail.com
        if (!adminUserRepository.existsByEmail("gurijalanag2146@gmail.com")) {
            AdminUser admin2 = AdminUser.builder()
                .email("gurijalanag2146@gmail.com")
                .name("Nageswara Rao Gurijala")
                .password(passwordEncoder.encode("Nag@2218"))
                .role(AdminUser.Role.ADMIN)
                .isActive(true)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            adminUserRepository.save(admin2);
            log.info("Created admin user: {}", admin2.getEmail());
        } else {
            log.info("Admin user already exists: gurijalanag2146@gmail.com");
        }
    }
    
    private void initializeHeaders() {
        // Header 1: Home
        if (!headersRepository.existsById("nav-home")) {
            Headers header1 = new Headers("nav-home", "Home", "hero", true, false);
            headersRepository.save(header1);
            log.info("Created header: nav-home");
        } else {
            log.info("Header already exists: nav-home");
        }
        
        // Header 2: About
        if (!headersRepository.existsById("nav-about")) {
            Headers header2 = new Headers("nav-about", "About", "about", true, false);
            headersRepository.save(header2);
            log.info("Created header: nav-about");
        } else {
            log.info("Header already exists: nav-about");
        }
        
        // Header 3: Services
        if (!headersRepository.existsById("nav-services")) {
            Headers header3 = new Headers("nav-services", "Services", "services", true, false);
            headersRepository.save(header3);
            log.info("Created header: nav-services");
        } else {
            log.info("Header already exists: nav-services");
        }
        
        // Header 4: Projects
        if (!headersRepository.existsById("nav-projects")) {
            Headers header4 = new Headers("nav-projects", "Projects", "demo-projects", true, false);
            headersRepository.save(header4);
            log.info("Created header: nav-projects");
        } else {
            log.info("Header already exists: nav-projects");
        }
        
        // Header 5: Products
        if (!headersRepository.existsById("nav-products")) {
            Headers header5 = new Headers("nav-products", "Products", "products", true, false);
            headersRepository.save(header5);
            log.info("Created header: nav-products");
        } else {
            log.info("Header already exists: nav-products");
        }
        
        // Header 6: Process
        if (!headersRepository.existsById("nav-process")) {
            Headers header6 = new Headers("nav-process", "Process", "process", true, false);
            headersRepository.save(header6);
            log.info("Created header: nav-process");
        } else {
            log.info("Header already exists: nav-process");
        }
        
        // Header 7: Careers
        if (!headersRepository.existsById("nav-careers")) {
            Headers header7 = new Headers("nav-careers", "Careers", "/careers", true, true);
            headersRepository.save(header7);
            log.info("Created header: nav-careers");
        } else {
            log.info("Header already exists: nav-careers");
        }
        
        // Header 8: Contact
        if (!headersRepository.existsById("nav-contact")) {
            Headers header8 = new Headers("nav-contact", "Contact", "contact", true, false);
            headersRepository.save(header8);
            log.info("Created header: nav-contact");
        } else {
            log.info("Header already exists: nav-contact");
        }
        
        // Header 9: Login
        if (!headersRepository.existsById("nav-login")) {
            Headers header9 = new Headers("nav-login", "Login", "/admin/login", true, true);
            headersRepository.save(header9);
            log.info("Created header: nav-login");
        } else {
            log.info("Header already exists: nav-login");
        }
        
        // Header 10: Language Switcher
        if (!headersRepository.existsById("nav-lang")) {
            Headers header10 = new Headers("nav-lang", "Language Switcher", "#", true, false);
            headersRepository.save(header10);
            log.info("Created header: nav-lang");
        } else {
            log.info("Header already exists: nav-lang");
        }
    }
}