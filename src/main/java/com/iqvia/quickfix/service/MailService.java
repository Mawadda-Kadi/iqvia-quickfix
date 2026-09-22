package com.iqvia.quickfix.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${app.mail.from}")
    private String mailFrom;
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTicketUpdateMail(
            String to,
            String subject,
            String message
    ) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);
        mail.setFrom(mailFrom);

        mailSender.send(mail);

    }

}
