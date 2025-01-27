package service;

import cz.upce.nnpro.bookbooking.Application;
import cz.upce.nnpro.bookbooking.dto.*;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import utils.TestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class ReviewServiceIntegrationTest {

    @Autowired private ReviewService reviewService;

    @Autowired private BookService bookService;

    @Autowired private AuthService authService;

    @Autowired private UserService userService;

    private final RegisterRequest testRegisterRequest = TestUtils.testRegisterRequest();

    private final Review testReview() {
        authService.register(TestUtils.testRegisterRequest());
        AppUser user = userService.getByUsername(TestUtils.testRegisterRequest().getUsername());

        Book book = bookService.create(TestUtils.testBook("Book 1"));
        Review review = reviewService.create(TestUtils.testReview(book, user, 3));
        book.setReviews(Collections.singletonList(review));

        return review;
    }

    @Test
    void testGet() {
        Review review = testReview();
        Long userId = review.getUser().getId();
        Long bookId = review.getBook().getId();

        ResponseBookReviewDTO response = reviewService.get(review.getUser(), bookId);
        Review tested = reviewService.getById(response.getId());

        assertNotNull(response);
        assertEquals(userId, tested.getUser().getId());
        assertEquals(bookId, tested.getBook().getId());
    }

    @Test
    void testCreate() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());

        Book book = bookService.create(TestUtils.testBook("Book 2"));

        RequestBookReviewDTO reviewData = new RequestBookReviewDTO(5, "Great book!");

        ResponseBookReviewDTO response = reviewService.create(user, book, reviewData);

        assertNotNull(response);
        assertEquals("Great book!", response.getText());
        assertEquals(5, response.getRating());
    }

    @Test
    void testUpdate() {
        Review review = testReview();
        Long bookId = review.getBook().getId();

        RequestBookReviewDTO updatedReviewData = new RequestBookReviewDTO(4, "Updated review text");

        ResponseBookReviewDTO response = reviewService.update(review.getUser(), bookId, updatedReviewData);

        assertNotNull(response);
        assertEquals("Updated review text", response.getText());
        assertEquals(4, response.getRating());
    }

    @Test
    void testDelete() {
        Review review = testReview();
        Long bookId = review.getBook().getId();

        reviewService.delete(review.getUser(), bookId);

        assertThrows(CustomExceptionHandler.EntityNotFoundException.class, () -> reviewService.getByUserIdAndBookId(review.getUser().getId(), bookId));
    }

    @Test
    void testGetAllByBookId() {
        Book book = bookService.create(TestUtils.testBook("Book 3"));

        RegisterRequest request1 = testRegisterRequest;
        request1.setUsername("firstUser");
        request1.setEmail("first@user.com");
        authService.register(request1);
        AppUser user1 = userService.getByUsername(request1.getUsername());
        reviewService.create(user1, book, new RequestBookReviewDTO(4, "Good read"));

        RegisterRequest request2 = testRegisterRequest;
        request2.setUsername("secondUser");
        request2.setEmail("second@user.com");
        authService.register(request2);
        AppUser user2 = userService.getByUsername(request2.getUsername());
        reviewService.create(user2, book, new RequestBookReviewDTO(3, "Not bad"));

        List<Review> reviews = reviewService.getAllByBookId(book.getId());

        assertEquals(2, reviews.size());
    }

    @Test
    void testGetAllByBookId_Empty() {
        Book book = bookService.create(TestUtils.testBook("Book 4"));

        List<Review> reviews = reviewService.getAllByBookId(book.getId());

        assertTrue(reviews.isEmpty());
    }

    @Test
    void testGetByUserIdAndBookId_NotFound() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());
        Book book = bookService.create(TestUtils.testBook("Book 5"));

        assertThrows(CustomExceptionHandler.EntityNotFoundException.class, () -> reviewService.getByUserIdAndBookId(user.getId(), book.getId()));
    }

    @Test
    void testCreate_AnotherReview_SameUser_SameBook_Updated() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());

        Book book = bookService.create(TestUtils.testBook("Book 7"));

        RequestBookReviewDTO firstReviewData = new RequestBookReviewDTO(3, "First review");
        ResponseBookReviewDTO firstResponse = reviewService.create(user, book, firstReviewData);

        assertNotNull(firstResponse);
        assertEquals("First review", firstResponse.getText());
        assertEquals(3, firstResponse.getRating());

        RequestBookReviewDTO secondReviewData = new RequestBookReviewDTO(4, "Updated review");
        ResponseBookReviewDTO secondResponse = reviewService.create(user, book, secondReviewData);

        assertNotNull(secondResponse);
        assertEquals("Updated review", secondResponse.getText());
        assertEquals(4, secondResponse.getRating());

        List<Review> reviews = reviewService.getAllByBookId(book.getId());
        assertEquals(1, reviews.size());
        assertEquals("Updated review", reviews.getFirst().getText());
        assertEquals(4, reviews.getFirst().getRating());
    }

}
