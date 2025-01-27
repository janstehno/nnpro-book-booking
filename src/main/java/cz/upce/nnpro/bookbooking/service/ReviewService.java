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
import java.util.Optional;

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

    public ResponseBookReviewDTO get(AppUser user, Long bookId) throws RuntimeException {
        final Review review = getByUserIdAndBookId(user.getId(), bookId);
        return new ResponseBookReviewDTO(review);
    }

    public ResponseBookReviewDTO create(AppUser user, Book book, RequestBookReviewDTO data) {
        Optional<Review> existingReview = reviewRepository.findByUserIdAndBookId(user.getId(), book.getId());
        Review review;

        if (existingReview.isPresent()) {
            review = existingReview.get();
            review.setRating(data.getRating());
            review.setText(data.getText());
            update(review);
        } else {
            review = create(new Review(data.getText(), data.getRating(), user, book));
        }

        return new ResponseBookReviewDTO(review);
    }

    public ResponseBookReviewDTO update(AppUser user, Long bookId, RequestBookReviewDTO data) {
        final Review review = getByUserIdAndBookId(user.getId(), bookId);
        review.setRating(data.getRating());
        review.setText(data.getText());
        update(review);
        return new ResponseBookReviewDTO(review);
    }

    public void delete(AppUser user, Long bookId) {
        final Review review = getByUserIdAndBookId(user.getId(), bookId);
        deleteById(review.getId());
    }
}
