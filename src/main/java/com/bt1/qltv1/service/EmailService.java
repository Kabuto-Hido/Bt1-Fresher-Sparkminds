package com.bt1.qltv1.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendAsHtml(String sendTo, String template, String subject);
    void sendSimpleEmail(String toEmail, String subject, String body);
}
