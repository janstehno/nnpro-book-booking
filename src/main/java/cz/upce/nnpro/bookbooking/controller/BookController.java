package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.BookDetailDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        final List<Book> books = service.getAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDTO> getBookById(
            @PathVariable
            Long id) {
        final Book book = service.getById(id);
        if (book != null) {
            final List<Review> reviews = reviewService.getAllByBookId(book.getId());
            return ResponseEntity.ok(BookDetailDTO.builder().book(book).reviews(reviews).build());
        }
        return ResponseEntity.notFound().build();
    }

}

