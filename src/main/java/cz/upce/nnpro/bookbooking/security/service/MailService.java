package cz.upce.nnpro.bookbooking.security.service;

import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${service.mail.enabled}") private boolean isServiceMailEnabled;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailAboutPasswordReset(String to, String resetUrl) {
        if (!isServiceMailEnabled) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Book Booking");
        message.setTo(to);
        message.setSubject("Password reset");
        message.setText("For password reset, use this link: " + resetUrl);
        mailSender.send(message);
    }

    public void sendEmailAboutPurchase(String to, Purchase purchase) {
        if (!isServiceMailEnabled) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Book Booking");
        message.setTo(to);
        message.setSubject("Thank you for your purchase!");

        StringBuilder content = new StringBuilder();
        content.append("Thank you for your purchase on ").append(purchase.getDate()).append(".\n\n");
        content.append("Purchase details:\n");

        content.append("Books purchased:\n");
        purchase.getBookPurchases().forEach(bookPurchase -> {
            content.append("- ").append(bookPurchase.getBook().getTitle()).append("\n");
        });

        content.append("Total Price: ").append(purchase.getPrice()).append(" USD\n");

        message.setText(content.toString());
        mailSender.send(message);
    }

    public void sendEmailAboutOrder(String to, Order order) {
        if (!isServiceMailEnabled) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Book Booking");
        message.setTo(to);
        message.setSubject("Your Order Confirmation");

        StringBuilder content = new StringBuilder();
        content.append("Thank you for your order on ").append(order.getDate()).append(".\n\n");
        content.append("Order details:\n");

        content.append("- Book ")
               .append(" | ")
               .append("  Quantity ")
               .append(" | ")
               .append("  Status ")
               .append(" | ")
               .append("  Booking Date ")
               .append(" | ")
               .append("  Expiration Date ")
               .append("\n");

        order.getBookings().forEach(booking -> {
            content.append(booking.getBook().getTitle())
                   .append(" | ")
                   .append(booking.getCount())
                   .append(" | ")
                   .append(booking.getStatus())
                   .append(" | ")
                   .append(booking.getBookingDate())
                   .append(" | ")
                   .append(booking.getExpirationDate())
                   .append("\n");
        });

        message.setText(content.toString());
        mailSender.send(message);
    }

}
