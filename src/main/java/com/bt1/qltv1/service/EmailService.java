package com.bt1.qltv1.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailService {
    void sendAsHtml(String sendTo, String body, String subject);
    void sendSimpleEmail(String toEmail, String subject, String body);
    void sendMailWithInline(String sendTo, String body, String subject, List<String> imgNames);

}
