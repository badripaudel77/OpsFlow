package com.miu.flowops.service;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
}
