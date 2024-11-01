package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Review;
import cz.upce.nnpro.bookbooking.repository.ReviewRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ReviewService implements ServiceInterface<Review> {

    private final ReviewRepository reviewRepository;

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
}
