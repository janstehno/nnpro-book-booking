package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.BookReviewDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import cz.upce.nnpro.bookbooking.entity.User;
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

    public List<Review> getAllByBookId(Long id) {
        return reviewRepository.findAllByBookId(id);
    }

    @Override
    public Review getById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public Review create(Review review) {
        return reviewRepository.save(review);
    }

    public Review create(User user, Book book, BookReviewDTO data) {
        final Review review = Review.builder().user(user).book(book).rating(data.getRating()).text(data.getText()).build();
        return reviewRepository.save(review);
    }

    @Override
    public Review update(Review review) {
        return reviewRepository.save(review);
    }

    public Review update(Review review, BookReviewDTO data) {
        review.setRating(data.getRating());
        review.setText(data.getText());
        return reviewRepository.save(review);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    public Review getByUserIdAndBookId(Long userId, Long bookId) {
        return reviewRepository.findByUserIdAndBookId(userId, bookId);
    }
}
