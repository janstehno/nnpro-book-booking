package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookReviewDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService implements ServiceInterface<Book> {

    private final BookRepository bookRepository;

    private final ReviewService reviewService;

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

    public ResponseBookDetailDTO get(Long bookId) {
        final Book book = getById(bookId);
        final List<ResponseBookReviewDTO> reviews = reviewService.getAllByBookId(book.getId())
                                                                 .stream()
                                                                 .map(r -> new ResponseBookReviewDTO(r.getId(),
                                                                                                     r.getUser().getFirstname(),
                                                                                                     r.getUser().getLastname(),
                                                                                                     r.getRating(),
                                                                                                     r.getText(),
                                                                                                     r.getDate()))
                                                                 .toList();
        return new ResponseBookDetailDTO(book, reviews);
    }
}
