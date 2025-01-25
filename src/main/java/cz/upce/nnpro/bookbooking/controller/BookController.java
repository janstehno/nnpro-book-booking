package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.ResponseBookDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/best")
    public ResponseEntity<List<ResponseBookDTO>> getBestBooks(
            @RequestParam(required = false, defaultValue = "10")
            int limit) {
        return ResponseEntity.ok(service.getBest(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBookDetailDTO> getBookById(
            @PathVariable
            Long id) {
        return ResponseEntity.ok(service.get(id));
    }

}

