package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Book;
import lombok.Data;

@Data
public class ResponseBookDTO {
    private Long id;
    private String title;
    private String author;
    private Integer available;
    private Boolean online;
    private Boolean ebook;
    private Double price;
    private Double rating;

    public ResponseBookDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.available = book.getAvailableCopies();
        this.online = book.isOnline();
        this.ebook = book.isEbook();
        this.price = book.getEbookPrice();
        this.rating = book.getRating();
    }
}
