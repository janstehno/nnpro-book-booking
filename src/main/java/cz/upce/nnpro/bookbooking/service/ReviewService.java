package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.RequestBookReviewDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookReviewDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService implements ServiceInterface<Review> {

    private final ReviewRepository reviewRepository;

    private final BookService bookService;

    @Override
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Review getById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public Review create(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review update(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    public List<Review> getAllByBookId(Long id) {
        return reviewRepository.findAllByBookId(id);
    }

    public Review getByUserIdAndBookId(Long userId, Long bookId) {
        return reviewRepository.findByUserIdAndBookId(userId, bookId);
    }

    public ResponseBookReviewDTO create(AppUser user, Long bookId, RequestBookReviewDTO data) throws RuntimeException {
        final Book book = bookService.getById(bookId);
        if (book == null) throw new CustomExceptionHandler.ItemNotFoundException();

        final Review review = Review.builder().user(user).book(book).rating(data.getRating()).text(data.getText()).build();
        create(review);

        return new ResponseBookReviewDTO(user.getFirstname(), user.getLastname(), review.getRating(), review.getText());
    }

    public ResponseBookReviewDTO update(AppUser user, Long bookId, RequestBookReviewDTO data) throws RuntimeException {
        final Review review = getByUserIdAndBookId(user.getId(), bookId);
        if (review == null) throw new CustomExceptionHandler.ItemNotFoundException();

        review.setRating(data.getRating());
        review.setText(data.getText());
        update(review);

        return new ResponseBookReviewDTO(user.getFirstname(), user.getLastname(), review.getRating(), review.getText());
    }

    public void delete(AppUser user, Long bookId) throws RuntimeException {
        final Review review = getByUserIdAndBookId(user.getId(), bookId);
        if (review == null) throw new CustomExceptionHandler.ItemNotFoundException();

        deleteById(review.getId());
    }
}
