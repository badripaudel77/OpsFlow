package com.miu.flowops.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.miu.flowops.service.IEmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String message) {
        sendEmail(to, subject, message, null, null, null);
    }

    @Override
    public void sendEmail(String to, String subject, String message, String releaseId, String taskId, String developerId) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("noreply@opsflow.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(buildHtmlTemplate(subject, message, releaseId, taskId, developerId), true); // true = HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildHtmlTemplate(String subject, String message, String releaseId, String taskId, String developerId) {
        StringBuilder detailsHtml = new StringBuilder();
        detailsHtml.append("<div style='margin-top: 20px; padding: 15px; background-color: #f9f9f9; border-left: 4px solid #4A90A4;'>");
        
        if (releaseId != null && !releaseId.isEmpty()) {
            detailsHtml.append("<p style='margin: 5px 0;'><strong>Release ID:</strong> ").append(releaseId).append("</p>");
        }
        if (taskId != null && !taskId.isEmpty()) {
            detailsHtml.append("<p style='margin: 5px 0;'><strong>Task ID:</strong> ").append(taskId).append("</p>");
        }
        if (developerId != null && !developerId.isEmpty()) {
            detailsHtml.append("<p style='margin: 5px 0;'><strong>Developer ID:</strong> ").append(developerId).append("</p>");
        }
        detailsHtml.append("</div>");

        // If no details are present, we might want to avoid showing the empty div, but the check is simple enough.
        // Actually, if all are null, the div will be empty. Let's check if we should append the div at all.
        boolean hasDetails = (releaseId != null && !releaseId.isEmpty()) || 
                             (taskId != null && !taskId.isEmpty()) || 
                             (developerId != null && !developerId.isEmpty());
        
        String detailsSection = hasDetails ? detailsHtml.toString() : "";

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='margin:0; padding:0; font-family:Arial,sans-serif; background-color:#f4f4f4;'>" +
                "<table width='100%' style='max-width:600px; margin:0 auto; background-color:#ffffff;'>" +
                "<tr><td style='background-color:#4A90A4; padding:30px; text-align:center;'>" +
                "<h2 style='color:#ffffff; margin:0;'>" + subject + "</h2>" +
                "</td></tr>" +
                "<tr><td style='padding:40px 30px;'>" +
                "<p style='color:#333; font-size:16px; line-height:1.6;'>" + message + "</p>" +
                detailsSection +
                "</td></tr>" +
                "<tr><td style='background-color:#f8f8f8; padding:20px; text-align:center;'>" +
                "<p style='color:#888; font-size:12px;'>Don't replay to this mail, it is an automated message.</p>" +
                "</td></tr>" +
                "</table></body></html>";
    }

}
