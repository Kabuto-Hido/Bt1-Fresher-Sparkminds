package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
@Log4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;

    @Override
    public void sendSimpleEmail(String toEmail,
                                String subject,
                                String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nogbuituananh@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        emailSender.send(message);
    }

    @Override
    public void sendMailWithInline(String sendTo, String body, String subject) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress("ngobutuananh@gmail.com",
                    "Sparkminds Library"));

            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setText(body, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Gmail send fail");
        }

        emailSender.send(message);
    }

    @Override
    public void sendAsHtml(String sendTo, String body, String subject) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress("ngobutuananh@gmail.com",
                    "Sparkminds Library"));

            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setText(body, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Gmail send fail");
        }

        emailSender.send(message);
    }
}
