package cz.upce.nnpro.bookbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseBookDTO {
    private Long id;
    private String title;
    private String author;
    private double rating;
}
