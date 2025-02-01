package entity;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
public class BookTest {

    private Book book;
    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    public void setUp() {
        book = new Book();
    }

    @Test
    public void testGetRating_NoReviews() {
        book.setReviews(List.of());
        assertEquals(0.0, book.getRating(), "Rating should be 0.0 when there are no reviews.");
    }

    @Test
    public void testGetRating_SingleReview() {
        review1 = mock(Review.class);
        when(review1.getRating()).thenReturn(4);

        book.setReviews(List.of(review1));
        assertEquals(4.0, book.getRating(), "Rating should be 4.0 when there is one review with rating 4.");
    }

    @Test
    public void testGetRating_TwoReviews() {
        review1 = mock(Review.class);
        review2 = mock(Review.class);

        when(review1.getRating()).thenReturn(4);
        when(review2.getRating()).thenReturn(2);

        book.setReviews(List.of(review1, review2));
        assertEquals(3.0, book.getRating(), "Rating should be 3.0 when there are two reviews with ratings 4 and 2.");
    }

    @Test
    public void testGetRating_ThreeReviews() {
        review1 = mock(Review.class);
        review2 = mock(Review.class);
        review3 = mock(Review.class);

        when(review1.getRating()).thenReturn(1);
        when(review2.getRating()).thenReturn(0);
        when(review3.getRating()).thenReturn(2);

        book.setReviews(List.of(review1, review2, review3));
        assertEquals(1.0, book.getRating(), "Rating should be 1.0 when there are three reviews with ratings 0, 1 and 2.");
    }

    @Test
    public void testGetRating_AllZeroRatings() {
        review1 = mock(Review.class);
        review2 = mock(Review.class);
        review3 = mock(Review.class);

        when(review1.getRating()).thenReturn(0);
        when(review2.getRating()).thenReturn(0);
        when(review3.getRating()).thenReturn(0);

        book.setReviews(List.of(review1, review2, review3));
        assertEquals(0.0, book.getRating(), "Rating should be 0.0 when all reviews have a rating of 0.");
    }

    @Test
    public void testGetRating_TwoReviewsSomeZeroRatings() {
        review1 = mock(Review.class);
        review2 = mock(Review.class);

        when(review1.getRating()).thenReturn(0);
        when(review2.getRating()).thenReturn(5);

        book.setReviews(List.of(review1, review2));
        assertEquals(2.5, book.getRating(), "Rating should be 2.5 when there are reviews with ratings 0 and 5.");
    }

    @Test
    public void testGetRating_ThreeReviewsSomeZeroRatings() {
        review1 = mock(Review.class);
        review2 = mock(Review.class);
        review3 = mock(Review.class);

        when(review1.getRating()).thenReturn(0);
        when(review2.getRating()).thenReturn(0);
        when(review3.getRating()).thenReturn(3);

        book.setReviews(List.of(review1, review2, review3));
        assertEquals(1.0, book.getRating(), "Rating should be 1.0 when there are reviews with ratings 0, 0 and 3.");
    }
}
