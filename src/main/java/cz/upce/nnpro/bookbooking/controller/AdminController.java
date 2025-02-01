package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.BookDTO;
import cz.upce.nnpro.bookbooking.dto.RequestBookingsDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseUserDTO;
import cz.upce.nnpro.bookbooking.service.AdminService;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service;

    private final UserService userService;

    private final BookService bookService;

    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<List<ResponseBookingDTO>> getAllBookingsOfUser(
            @PathVariable
            Long userId) {
        return ResponseEntity.ok(service.getAllByUserId(userId));
    }

    @PutMapping("/users/{userId}/bookings")
    public ResponseEntity<List<ResponseBookingDTO>> updateBookings(
            @PathVariable
            Long userId,
            @Valid
            @RequestBody
            RequestBookingsDTO requestBookingsDTO) {
        service.updateReturnedBooks(userId, requestBookingsDTO.getReturnIds());
        service.updateLoanedBooks(userId, requestBookingsDTO.getLoanIds());
        return getAllBookingsOfUser(userId);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/books")
    public ResponseEntity<BookDTO> createBook(
            @Valid
            @RequestBody
            BookDTO book) {
        return ResponseEntity.ok(bookService.createBook(book));
    }

    @PutMapping("/books")
    public ResponseEntity<BookDTO> updateBook(
            @Valid
            @RequestBody
            BookDTO book) {
        return ResponseEntity.ok(bookService.updateBook(book));
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

}
