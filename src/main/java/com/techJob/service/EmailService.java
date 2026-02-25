package com.techJob.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.techJob.domain.entity.EmailVerificationToken;
import com.techJob.domain.entity.User;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    private static final String linkVerficationBaseUrl = "https://localhost:7777/api/v1/auth/verify-email?token=";
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(fromEmail);

        mailSender.send(message);
    }
    public void sendVerificationEmail(User user, EmailVerificationToken token) {
        String subject = "تأكيد بريدك الإلكتروني";
        String confirmationUrl = linkVerficationBaseUrl + token.getToken();

        String message = "اضغط على الرابط التالي لتفعيل حسابك:\n" + confirmationUrl +
                         "\nالرابط صالح لمدة 24 ساعة.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(fromEmail);

        mailSender.send(email);
    }
}
