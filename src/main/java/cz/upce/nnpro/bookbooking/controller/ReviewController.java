package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.BookReviewDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.ReviewService;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/books/{bookId}/review")
public class ReviewController {

    private final ReviewService service;

    private final BookService bookService;

    private final UserService userService;

    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @PathVariable
            Long bookId,
            @RequestBody
            @Valid
            BookReviewDTO data,
            @RequestHeader("Authorization")
            String token) {
        final Book book = bookService.getById(bookId);
        if (book == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        final AppUser user = userService.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final Review review = service.create(user, book, data);
        return ResponseEntity.ok(review);
    }

    @PutMapping
    public ResponseEntity<Review> updateReview(
            @PathVariable
            Long bookId,
            @RequestBody
            @Valid
            BookReviewDTO data,
            @RequestHeader("Authorization")
            String token) {
        final AppUser user = userService.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final Review foundReview = service.getByUserIdAndBookId(user.getId(), bookId);
        if (foundReview == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        final Review review = service.update(foundReview, data);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteReview(
            @PathVariable
            Long bookId,
            @RequestHeader("Authorization")
            String token) {
        final Review review = service.getByUserIdAndBookId(jwtService.extractUserId(token), bookId);
        if (review == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        service.deleteById(review.getId());
        return ResponseEntity.ok().build();
    }

}

