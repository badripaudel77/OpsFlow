package com.miu.flowops.service;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmail(String to, String subject, String body, String releaseId, String taskId, String developerId);
}
