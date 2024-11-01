package cz.upce.nnpro.bookbooking.repository;

import cz.upce.nnpro.bookbooking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {}
