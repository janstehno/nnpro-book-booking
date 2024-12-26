package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ResponseBookDetailDTO {
    private Book book;
    private List<ResponseBookReviewDTO> reviews;
}
