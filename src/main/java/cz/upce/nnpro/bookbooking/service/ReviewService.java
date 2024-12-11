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

    @Override
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Review getById(Long id) throws RuntimeException {
        return reviewRepository.findById(id).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
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

    public Review getByUserIdAndBookId(Long userId, Long bookId) throws RuntimeException {
        return reviewRepository.findByUserIdAndBookId(userId, bookId).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }

    public ResponseBookReviewDTO create(AppUser user, Book book, RequestBookReviewDTO data) {
        final Review review = Review.builder().user(user).book(book).rating(data.getRating()).text(data.getText()).build();
        create(review);
        return new ResponseBookReviewDTO(user.getFirstname(), user.getLastname(), review.getRating(), review.getText());
    }

    public ResponseBookReviewDTO update(AppUser user, Long bookId, RequestBookReviewDTO data) {
        final Review review = getByUserIdAndBookId(user.getId(), bookId);
        review.setRating(data.getRating());
        review.setText(data.getText());
        update(review);
        return new ResponseBookReviewDTO(user.getFirstname(), user.getLastname(), review.getRating(), review.getText());
    }

    public void delete(AppUser user, Long bookId) {
        final Review review = getByUserIdAndBookId(user.getId(), bookId);
        deleteById(review.getId());
    }
}
