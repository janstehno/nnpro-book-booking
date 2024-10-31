package cz.upce.nnpro.bookbooking.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Book Booking");
        message.setTo(to);
        message.setSubject("Resetování hesla");
        message.setText("Pro resetování hesla klikněte na následující odkaz: " + resetUrl);
        mailSender.send(message);
    }
}
