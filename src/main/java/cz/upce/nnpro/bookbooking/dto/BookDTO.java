package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    @Nullable private Long id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private boolean isOnline;
    private int physicalCopies;
    private double ebookPrice;

    public BookDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.genre = book.getGenre().name();
        this.description = book.getDescription();
        this.isOnline = book.isOnline();
        this.physicalCopies = book.getPhysicalCopies();
        this.ebookPrice = book.getEbookPrice();
    }

    public Book toBook() {
        Book book = new Book();
        if (id != null) book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(getGenre());
        book.setDescription(description);
        book.setOnline(isOnline);
        book.setPhysicalCopies(physicalCopies);
        book.setEbookPrice(ebookPrice);
        return book;
    }

    public GenreE getGenre() {
        return GenreE.valueOf(genre);
    }
}
