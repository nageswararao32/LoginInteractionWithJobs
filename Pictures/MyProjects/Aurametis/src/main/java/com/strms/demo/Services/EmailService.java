package com.strms.demo.Services;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.strms.demo.Entites.AdminUser;
import com.strms.demo.Repositories.AdminUserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired

    private AdminUserRepository adminUserRepository;
    
    @Async
    public void sendOtpEmail(String toEmail, String otp, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Your NxtGenAIT Admin Login OTP");
            
            String htmlContent = buildOtpEmailTemplate(otp, name);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("OTP email sent successfully to {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
    
    @Async
    public void sendPasswordResetEmail(String toEmail, String otp, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("NxtGenAIT Password Reset Request");
            
            String htmlContent = buildPasswordResetTemplate(otp, name);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Password reset email sent successfully to {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    private String buildOtpEmailTemplate(String otp, String name) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <tr>
                                    <td style="padding: 40px 40px 20px 40px; text-align: center; background-color: #2563eb; border-radius: 8px 8px 0 0;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 28px;">Aurametis</h1>
                                        <p style="margin: 10px 0 0 0; color: #e0e7ff; font-size: 14px;">Admin Portal</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 40px;">
                                        <h2 style="margin: 0 0 20px 0; color: #1e293b; font-size: 24px;">Hello %s,</h2>
                                        <p style="margin: 0 0 20px 0; color: #475569; font-size: 16px; line-height: 24px;">
                                            Your One-Time Password (OTP) for logging into the Aurametis Admin Portal is:
                                        </p>
                                        <div style="background-color: #f1f5f9; border-radius: 8px; padding: 30px; text-align: center; margin: 30px 0;">
                                            <p style="margin: 0 0 10px 0; color: #64748b; font-size: 14px; text-transform: uppercase; letter-spacing: 1px;">Your OTP Code</p>
                                            <p style="margin: 0; color: #2563eb; font-size: 48px; font-weight: bold; letter-spacing: 8px;">%s</p>
                                        </div>
                                        <p style="margin: 20px 0; color: #475569; font-size: 14px; line-height: 20px;">
                                            <strong>Important:</strong> This OTP will expire in 5 minutes. Do not share this code with anyone.
                                        </p>
                                        <p style="margin: 20px 0 0 0; color: #64748b; font-size: 13px;">
                                            If you didn't request this OTP, please ignore this email or contact support immediately.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 20px 40px; background-color: #f8fafc; border-radius: 0 0 8px 8px;">
                                        <p style="margin: 0; color: #94a3b8; font-size: 12px; text-align: center;">
                                            © 2026 Aurametis. All rights reserved.<br>
                                            This email was sent at %s
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(name, otp, timestamp);
    }
    
    private String buildPasswordResetTemplate(String otp, String name) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                <tr>
                                    <td style="padding: 40px 40px 20px 40px; text-align: center; background-color: #dc2626; border-radius: 8px 8px 0 0;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 28px;">Password Reset</h1>
                                        <p style="margin: 10px 0 0 0; color: #fecaca; font-size: 14px;">NxtGenAIT Admin Portal</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 40px;">
                                        <h2 style="margin: 0 0 20px 0; color: #1e293b; font-size: 24px;">Hello %s,</h2>
                                        <p style="margin: 0 0 20px 0; color: #475569; font-size: 16px; line-height: 24px;">
                                            We received a request to reset your password. Use the OTP below to proceed:
                                        </p>
                                        <div style="background-color: #fef2f2; border-radius: 8px; padding: 30px; text-align: center; margin: 30px 0; border: 2px solid #fecaca;">
                                            <p style="margin: 0 0 10px 0; color: #991b1b; font-size: 14px; text-transform: uppercase; letter-spacing: 1px;">Reset OTP Code</p>
                                            <p style="margin: 0; color: #dc2626; font-size: 48px; font-weight: bold; letter-spacing: 8px;">%s</p>
                                        </div>
                                        <p style="margin: 20px 0; color: #475569; font-size: 14px; line-height: 20px;">
                                            <strong>Security Notice:</strong> This OTP will expire in 5 minutes. Never share this code with anyone.
                                        </p>
                                        <p style="margin: 20px 0 0 0; color: #dc2626; font-size: 13px; background-color: #fef2f2; padding: 15px; border-radius: 6px;">
                                            ⚠️ If you didn't request a password reset, please secure your account immediately and contact support.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 20px 40px; background-color: #f8fafc; border-radius: 0 0 8px 8px;">
                                        <p style="margin: 0; color: #94a3b8; font-size: 12px; text-align: center;">
                                            © 2026 Aurametis. All rights reserved.<br>
                                            This email was sent at %s
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(name, otp, timestamp);
    }


    public void sendApplicationNotificationToAdmin(String applicantName, String jobTitle, 
                                                   String applicantEmail, String linkedinUrl, 
                                                   Double experience, byte[] resumeData, 
                                                   String resumeFileName) throws MessagingException {
        
        // Get all admin users
        List<AdminUser> adminUsers = adminUserRepository.findAll();
        
        // Send email to each admin
        for (AdminUser admin : adminUsers) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                
                helper.setFrom(fromEmail);
                helper.setTo(admin.getEmail());
                helper.setSubject("New Job Application: " + jobTitle);
                
                String htmlContent = buildAdminEmailTemplate(applicantName, jobTitle, applicantEmail, 
                                                             linkedinUrl, experience);
                helper.setText(htmlContent, true);
                
                // Attach resume to admin email
                if (resumeData != null && resumeData.length > 0) {
                    helper.addAttachment(resumeFileName, new ByteArrayResource(resumeData));
                }
                
                mailSender.send(message);
                System.out.println("Email sent successfully to admin: " + admin.getEmail());
                
            } catch (Exception e) {
                System.err.println("Failed to send email to admin: " + admin.getEmail() + " - " + e.getMessage());
                // Continue sending to other admins even if one fails
            }
        }
    }
    
    public void sendConfirmationToApplicant(String applicantEmail, String applicantName, 
                                           String jobTitle) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(applicantEmail);
        helper.setSubject("Application Received - " + jobTitle);
        
        String htmlContent = buildApplicantEmailTemplate(applicantName, jobTitle);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    private String buildAdminEmailTemplate(String name, String jobTitle, String email, 
                                          String linkedin, Double experience) {
        return "<!DOCTYPE html>" +
               "<html><head><style>" +
               "body{font-family:Arial,sans-serif;background-color:#f4f4f4;padding:20px;}" +
               ".container{background-color:#ffffff;padding:30px;border-radius:8px;max-width:600px;margin:0 auto;box-shadow:0 2px 10px rgba(0,0,0,0.1);}" +
               "h2{color:#2563eb;border-bottom:3px solid #2563eb;padding-bottom:10px;}" +
               ".info-row{margin:15px 0;padding:10px;background-color:#f8fafc;border-left:4px solid #2563eb;}" +
               ".label{font-weight:bold;color:#475569;display:inline-block;width:150px;}" +
               ".value{color:#1e293b;}" +
               ".footer{margin-top:30px;padding-top:20px;border-top:1px solid #e2e8f0;color:#64748b;font-size:12px;text-align:center;}" +
               "</style></head><body>" +
               "<div class='container'>" +
               "<h2>🎯 New Job Application Received</h2>" +
               "<div class='info-row'><span class='label'>Position:</span><span class='value'>" + jobTitle + "</span></div>" +
               "<div class='info-row'><span class='label'>Applicant Name:</span><span class='value'>" + name + "</span></div>" +
               "<div class='info-row'><span class='label'>Email:</span><span class='value'><a href='mailto:" + email + "'>" + email + "</a></span></div>" +
               "<div class='info-row'><span class='label'>Experience:</span><span class='value'>" + experience + " years</span></div>" +
               "<div class='info-row'><span class='label'>LinkedIn:</span><span class='value'><a href='" + linkedin + "' target='_blank'>View Profile</a></span></div>" +
               "<p style='margin-top:20px;padding:15px;background-color:#dbeafe;border-radius:6px;'><strong>📎 Resume attached to this email</strong></p>" +
               "<div class='footer'>This is an automated notification from NextGenTech Career Portal</div>" +
               "</div></body></html>";
    }
    
    private String buildApplicantEmailTemplate(String name, String jobTitle) {
        return "<!DOCTYPE html>" +
               "<html><head><style>" +
               "body{font-family:Arial,sans-serif;background-color:#f4f4f4;padding:20px;}" +
               ".container{background-color:#ffffff;padding:30px;border-radius:8px;max-width:600px;margin:0 auto;box-shadow:0 2px 10px rgba(0,0,0,0.1);}" +
               "h2{color:#2563eb;}" +
               ".message{color:#475569;line-height:1.8;margin:20px 0;}" +
               ".highlight{background-color:#dbeafe;padding:15px;border-radius:6px;margin:20px 0;}" +
               ".footer{margin-top:30px;padding-top:20px;border-top:1px solid #e2e8f0;color:#64748b;font-size:12px;}" +
               "</style></head><body>" +
               "<div class='container'>" +
               "<h2>✅ Application Received Successfully</h2>" +
               "<div class='message'>" +
               "<p>Dear " + name + ",</p>" +
               "<p>Thank you for applying for the <strong>" + jobTitle + "</strong> position at NextGenTech.</p>" +
               "<div class='highlight'>We have successfully received your application and our team will review it shortly. " +
               "If your profile matches our requirements, we will contact you within 5-7 business days.</div>" +
               "<p>In the meantime, feel free to explore our website and learn more about what we do.</p>" +
               "<p>Best regards,<br><strong>NextGenTech Hiring Team</strong></p>" +
               "</div>" +
               "<div class='footer'>© 2025 NextGenTech. All rights reserved.</div>" +
               "</div></body></html>";
    }

   public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);    
        } catch (Exception e) {
            System.err.println("✗ Error sending HTML email to " + to);
            System.err.println("Error Details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
