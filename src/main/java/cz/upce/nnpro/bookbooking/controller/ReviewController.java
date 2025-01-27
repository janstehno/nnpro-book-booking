package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.RequestBookReviewDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookReviewDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/books/{bookId}/review")
public class ReviewController {

    private final ReviewService service;

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ResponseBookReviewDTO> getReview(
            @PathVariable
            Long bookId,
            @AuthenticationPrincipal
            AppUser user) {
        try {
            return ResponseEntity.ok(service.get(user, bookId));
        } catch (Exception e) {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseBookReviewDTO> createReview(
            @PathVariable
            Long bookId,
            @RequestBody
            @Valid
            RequestBookReviewDTO data,
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.create(user, bookService.getById(bookId), data));
    }

    @PutMapping
    public ResponseEntity<ResponseBookReviewDTO> updateReview(
            @PathVariable
            Long bookId,
            @RequestBody
            @Valid
            RequestBookReviewDTO data,
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.update(user, bookId, data));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteReview(
            @PathVariable
            Long bookId,
            @AuthenticationPrincipal
            AppUser user) {
        service.delete(user, bookId);
        return ResponseEntity.ok().build();
    }

}

