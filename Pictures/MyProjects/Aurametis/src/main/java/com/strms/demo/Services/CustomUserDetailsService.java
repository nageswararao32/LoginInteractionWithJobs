package com.strms.demo.Services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.strms.demo.Repositories.AdminUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    private final AdminUserRepository adminUserRepository;
    
    public CustomUserDetailsService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        return adminUserRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.error("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found with email: " + email);
            });
    }
}
