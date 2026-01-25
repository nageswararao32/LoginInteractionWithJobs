package com.strms.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.strms.demo.Repositories.ContactRepository;
import com.strms.demo.Repositories.AdminUserRepository;
import com.strms.demo.Services.EmailService;
import com.strms.demo.DTOs.ContactRequest;
import com.strms.demo.DTOs.ContactResponse;
import com.strms.demo.DTOs.ContactUpdateRequest;
import com.strms.demo.Entites.AdminUser;
import com.strms.demo.Entites.Contact;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AdminUserRepository adminUserRepository;

    // Save new contact
    @PostMapping("/save")
    public ResponseEntity<ContactResponse> saveContact(@RequestBody ContactRequest contactRequest) {
        try {
            // Check if contact already exists with same email and name
            Optional<Contact> existingContact = contactRepository.findByEmailIgnoreCaseAndNameIgnoreCase(
                    contactRequest.getEmail(),
                    contactRequest.getName()
            );

            if (existingContact.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ContactResponse(false, "Contact with this email and name already exists", null));
            }

            // Create new contact
            Contact contact = new Contact();
            contact.setName(contactRequest.getName());
            contact.setEmail(contactRequest.getEmail());
            contact.setMessage(contactRequest.getMessage());

            // Save to database
            Contact savedContact = contactRepository.save(contact);

            // Send emails to all admins
            List<AdminUser> admins = adminUserRepository.findAll();

            if (!admins.isEmpty()) {
                for (AdminUser admin : admins) {
                    sendEmailToAdmin(admin, savedContact);
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ContactResponse(true, "Contact saved successfully and admins notified", savedContact));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ContactResponse(false, "Error saving contact: " + e.getMessage(), null));
        }
    }

    // Get all contacts (Admin only)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactResponse> getAllContacts() {
        try {
            List<Contact> contacts = contactRepository.findAll();
            return ResponseEntity.ok(new ContactResponse(true, "Contacts retrieved successfully", contacts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ContactResponse(false, "Error retrieving contacts: " + e.getMessage(), null));
        }
    }

    // Update contact status by ID (Admin only)
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactResponse> updateContact(@PathVariable Long id, @RequestBody ContactUpdateRequest updateRequest) {
        try {
            Optional<Contact> optionalContact = contactRepository.findById(id);

            if (optionalContact.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ContactResponse(false, "Contact not found", null));
            }

            Contact contact = optionalContact.get();
            
            if (updateRequest.getStatus() != null) {
                contact.setStatus(Contact.ContactStatus.valueOf(updateRequest.getStatus()));
            }

            Contact updatedContact = contactRepository.save(contact);

            return ResponseEntity.ok(new ContactResponse(true, "Contact updated successfully", updatedContact));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ContactResponse(false, "Error updating contact: " + e.getMessage(), null));
        }
    }

    // Helper method to send email to admin with HTML template
    private void sendEmailToAdmin(AdminUser admin, Contact contact) {
        try {
            String subject = "New Contact Form Submission - " + contact.getName();

            String htmlBody = buildContactEmailTemplate(admin.getName(), contact);

            emailService.sendHtmlEmail(admin.getEmail(), subject, htmlBody);

        } catch (Exception e) {
            System.err.println("Error sending email to admin " + admin.getEmail() + ": " + e.getMessage());
        }
    }

    // HTML Email Template for contact notification
    private String buildContactEmailTemplate(String adminName, Contact contact) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "<style>" +
               "body{font-family:Arial,sans-serif;background-color:#f5f5f5;padding:20px;margin:0;}" +
               ".container{background-color:#ffffff;padding:40px;border-radius:8px;max-width:600px;margin:0 auto;box-shadow:0 2px 10px rgba(0,0,0,0.1);}" +
               ".header{background:linear-gradient(135deg,#2563eb 0%,#1e40af 100%);color:#ffffff;padding:30px;border-radius:8px 8px 0 0;text-align:center;margin:-40px -40px 30px -40px;}" +
               ".header h1{margin:0;font-size:28px;}" +
               ".header p{margin:10px 0 0 0;opacity:0.9;}" +
               ".info-section{margin:25px 0;padding:20px;background-color:#f8fafc;border-left:4px solid #2563eb;border-radius:6px;}" +
               ".info-row{margin:12px 0;}" +
               ".label{font-weight:bold;color:#1e293b;display:inline-block;width:120px;}" +
               ".value{color:#475569;}" +
               ".message-section{margin:25px 0;padding:20px;background-color:#f0f9ff;border:1px solid #bfdbfe;border-radius:6px;}" +
               ".message-content{color:#1e293b;line-height:1.6;white-space:pre-wrap;word-wrap:break-word;}" +
               ".footer{margin-top:30px;padding-top:20px;border-top:1px solid #e2e8f0;color:#64748b;font-size:12px;text-align:center;}" +
               ".cta-button{display:inline-block;background-color:#2563eb;color:#ffffff;padding:12px 30px;border-radius:6px;text-decoration:none;margin-top:20px;font-weight:bold;}" +
               "</style></head><body>" +
               "<div class=\"container\">" +
               "<div class=\"header\">" +
               "<h1>📬 New Contact Submission</h1>" +
               "<p>You have received a new message from your contact form</p>" +
               "</div>" +
               "<p>Hello " + adminName + ",</p>" +
               "<p style=\"color:#475569;margin-bottom:25px;\">A new contact form submission has been received. Here are the details:</p>" +
               "<div class=\"info-section\">" +
               "<div class=\"info-row\"><span class=\"label\">👤 Name:</span><span class=\"value\">" + contact.getName() + "</span></div>" +
               "<div class=\"info-row\"><span class=\"label\">📧 Email:</span><span class=\"value\"><a href=\"mailto:" + contact.getEmail() + "\" style=\"color:#2563eb;text-decoration:none;\">" + contact.getEmail() + "</a></span></div>" +
               "<div class=\"info-row\"><span class=\"label\">🆔 Contact ID:</span><span class=\"value\">#" + contact.getId() + "</span></div>" +
               "<div class=\"info-row\"><span class=\"label\">📅 Submitted:</span><span class=\"value\">" + contact.getCreatedAt() + "</span></div>" +
               "<div class=\"info-row\"><span class=\"label\">📊 Status:</span><span class=\"value\" style=\"background-color:#fef3c7;padding:4px 8px;border-radius:4px;\">" + contact.getStatus() + "</span></div>" +
               "</div>" +
               "<div class=\"message-section\">" +
               "<h3 style=\"margin:0 0 15px 0;color:#1e293b;\">💬 Message:</h3>" +
               "<div class=\"message-content\">" + contact.getMessage() + "</div>" +
               "</div>" +
               "<p style=\"color:#475569;margin:20px 0;\">Please review this submission and respond to the contact as soon as possible.</p>" +
               "<a href=\"http://localhost:3000/admin/contactResp\" class=\"cta-button\">View in Admin Panel</a>" +
               "<div class=\"footer\">" +
               "<p>© 2026 NextGenTech. All rights reserved.<br>" +
               "This is an automated notification. Please do not reply to this email.</p>" +
               "</div>" +
               "</div>" +
               "</body></html>";
    }
}