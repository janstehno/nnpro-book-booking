package cz.upce.nnpro.bookbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseBookReviewDTO {
    private String firstname;
    private String lastname;
    private int rating;
    private String text;
}
