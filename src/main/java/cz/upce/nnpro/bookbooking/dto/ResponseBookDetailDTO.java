package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Book;
import lombok.Data;

import java.util.List;

@Data
public class ResponseBookDetailDTO {
    private Book book;
    private List<ResponseBookReviewDTO> reviews;

    public ResponseBookDetailDTO(Book book) {
        this.book = book;
        this.reviews = book.getReviews().stream().map(ResponseBookReviewDTO::new).toList();
    }
}
