package com.strms.demo.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strms.demo.Entites.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByStatus(Contact.ContactStatus status);
    List<Contact> findByEmailIgnoreCase(String email);
    Optional<Contact> findByEmailIgnoreCaseAndNameIgnoreCase(String email, String name);
    
}
