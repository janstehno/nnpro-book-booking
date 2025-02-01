package service;

import cz.upce.nnpro.bookbooking.dto.RequestBookReviewDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookReviewDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import cz.upce.nnpro.bookbooking.repository.ReviewRepository;
import cz.upce.nnpro.bookbooking.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ReviewServiceUnitTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private AppUser user;
    private Book book1;
    private Review review1;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@user.com");

        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book A");

        review1 = new Review("Great book!", 5, user, book1);
        review1.setId(1L);
        review1.setUser(user);
        review1.setBook(book1);
    }

    @Test
    void testGetAllReviews() {
        when(reviewRepository.findAll()).thenReturn(Collections.singletonList(review1));

        List<Review> result = reviewService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void testGetReviewById() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review1));

        Review result = reviewService.getById(1L);

        assertNotNull(result);
        assertEquals(review1, result);
    }

    @Test
    void testCreateReview() {
        RequestBookReviewDTO requestDTO = new RequestBookReviewDTO(review1.getRating(), review1.getText());
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);

        ResponseBookReviewDTO result = reviewService.create(user, book1, requestDTO);

        assertNotNull(result);
        assertEquals(review1.getText(), result.getText());
        assertEquals(review1.getRating(), result.getRating());
    }

    @Test
    void testCreateReview_UpdateExistingReview() {
        RequestBookReviewDTO requestDTO = new RequestBookReviewDTO(3, "Updated review");
        when(reviewRepository.findByUserIdAndBookId(user.getId(), book1.getId())).thenReturn(Optional.of(review1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);

        ResponseBookReviewDTO result = reviewService.create(user, book1, requestDTO);

        assertNotNull(result);
        assertEquals("Updated review", result.getText());
        assertEquals(3, result.getRating());
    }

    @Test
    void testUpdateReview() {
        RequestBookReviewDTO requestDTO = new RequestBookReviewDTO(4, "Updated review text");
        when(reviewRepository.findByUserIdAndBookId(user.getId(), book1.getId())).thenReturn(Optional.of(review1));
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);

        ResponseBookReviewDTO result = reviewService.update(user, book1.getId(), requestDTO);

        assertNotNull(result);
        assertEquals("Updated review text", result.getText());
        assertEquals(4, result.getRating());
    }

    @Test
    void testDeleteReview() {
        when(reviewRepository.findByUserIdAndBookId(user.getId(), book1.getId())).thenReturn(Optional.of(review1));
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.delete(user, book1.getId());

        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetReviewByUserIdAndBookId() {
        when(reviewRepository.findByUserIdAndBookId(user.getId(), book1.getId())).thenReturn(Optional.of(review1));

        Review result = reviewService.getByUserIdAndBookId(user.getId(), book1.getId());

        assertNotNull(result);
        assertEquals(review1, result);
    }

    @Test
    void testGetReviewByUserIdAndBookId_NotFound() {
        when(reviewRepository.findByUserIdAndBookId(user.getId(), book1.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reviewService.getByUserIdAndBookId(user.getId(), book1.getId()));
    }
}
