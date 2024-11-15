package cz.upce.nnpro.bookbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RequestBookReviewDTO {
    private int rating;
    private String text;
}