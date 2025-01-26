package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Review;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseBookReviewDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private int rating;
    private String text;
    private LocalDate date;

    public ResponseBookReviewDTO(Review review) {
        this.id = review.getId();
        this.firstname = review.getUser().getFirstname();
        this.lastname = review.getUser().getLastname();
        this.rating = review.getRating();
        this.text = review.getText();
        this.date = review.getDate();
    }
}
