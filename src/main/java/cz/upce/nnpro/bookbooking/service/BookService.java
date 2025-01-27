package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.ResponseBookDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBooksDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookService implements ServiceInterface<Book> {

    private final BookRepository bookRepository;

    @Override
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book getById(Long id) throws RuntimeException {
        return bookRepository.findById(id).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }

    @Override
    public Book create(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book update(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    public ResponseBookDetailDTO getBookDetail(Long bookId) {
        final Book book = getById(bookId);
        return new ResponseBookDetailDTO(book);
    }

    public List<ResponseBookDTO> getBest(int limit) {
        List<Book> allBooks = getAll();
        return allBooks.stream().sorted(Comparator.comparing(Book::getRating, Comparator.reverseOrder())).limit(limit).map(ResponseBookDTO::new).toList();
    }

    public ResponseBooksDTO getAllBooksFiltered(List<String> genres, String sort, int page, int size) {
        Pageable pageable = getAllBooksFilteredPageable(sort, page, size);

        Page<ResponseBookDTO> bookPage;
        Set<GenreE> genreEnums = genres.stream()
                                       .map(String::toUpperCase)
                                       .filter(name -> Arrays.stream(GenreE.values()).anyMatch(e -> e.name().equals(name)))
                                       .map(GenreE::valueOf)
                                       .collect(Collectors.toSet());
        if (!genreEnums.isEmpty()) {
            bookPage = bookRepository.findByGenreIn(genreEnums, pageable).map(ResponseBookDTO::new);
        } else {
            bookPage = bookRepository.findAll(pageable).map(ResponseBookDTO::new);
        }

        return new ResponseBooksDTO(bookPage.getContent(), bookPage.getTotalElements());
    }

    private Pageable getAllBooksFilteredPageable(String sortBy, int page, int size) {
        Sort sort = switch (sortBy) {
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "ebookPrice");
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "ebookPrice");
            default -> Sort.by(Sort.Direction.ASC, "title");
        };
        return PageRequest.of(page - 1, size, sort);
    }
}
