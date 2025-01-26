package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.ResponseBookDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBooksDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseGenreDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import cz.upce.nnpro.bookbooking.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    @GetMapping("/filtered")
    public ResponseEntity<ResponseBooksDTO> getALlBooksFiltered(
            @RequestParam(required = false)
            List<String> genres,
            @RequestParam(required = false, defaultValue = "name")
            String sort,
            @RequestParam(required = false, defaultValue = "1")
            int page,
            @RequestParam(required = false, defaultValue = "10")
            int size) {
        return ResponseEntity.ok(service.getAllBooksFiltered(genres, sort, page, size));
    }

    @GetMapping("/best")
    public ResponseEntity<List<ResponseBookDTO>> getBestBooks(
            @RequestParam(required = false, defaultValue = "10")
            int limit) {
        return ResponseEntity.ok(service.getBest(limit));
    }

    @GetMapping("/genres")
    public ResponseEntity<List<ResponseGenreDTO>> getBookGenres() {
        return ResponseEntity.ok(Arrays.stream(GenreE.values()).map(ResponseGenreDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBookDetailDTO> getBookById(
            @PathVariable
            Long id) {
        return ResponseEntity.ok(service.getBookDetail(id));
    }

}