package cz.upce.nnpro.bookbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ResponseBookReviewDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private int rating;
    private String text;
    private LocalDate date;
}
