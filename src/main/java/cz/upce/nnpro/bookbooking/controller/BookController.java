package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.service.BookService;
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

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBookDetailDTO> getBookById(
            @PathVariable
            Long id) {
        return ResponseEntity.ok(service.getBookById(id));
    }

}

