package com.hms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithPdf(String toEmail, String subject, String body, String pdfFilePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            // Attach the PDF file
            FileSystemResource file = new FileSystemResource(new File(pdfFilePath));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
            System.out.println("Email sent successfully with PDF attachment.");
        } catch (MessagingException e) {
            System.err.println("Error sending email with attachment: " + e.getMessage());
            throw new RuntimeException("Failed to send email with PDF attachment", e);
        }
    }
}

