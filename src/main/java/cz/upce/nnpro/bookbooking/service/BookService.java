package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
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
    public Book getById(Long id) {
        return bookRepository.findById(id).orElse(null);
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

    public ResponseBookDetailDTO getBookById(Long id) throws RuntimeException {
        final Book book = getById(id);
        if (book == null) throw new CustomExceptionHandler.ItemNotFoundException();
        final List<Review> reviews = reviewService.getAllByBookId(book.getId());
        return new ResponseBookDetailDTO(book, reviews);
    }
}
