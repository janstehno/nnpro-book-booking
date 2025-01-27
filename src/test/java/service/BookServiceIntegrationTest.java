package service;

import cz.upce.nnpro.bookbooking.Application;
import cz.upce.nnpro.bookbooking.dto.ResponseBookDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBooksDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.service.AuthService;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.ReviewService;
import cz.upce.nnpro.bookbooking.service.UserService;
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
class BookServiceIntegrationTest {

    @Autowired private AuthService authService;

    @Autowired private UserService userService;

    @Autowired private BookService bookService;

    @Autowired private ReviewService reviewService;

    private final Book testBook = TestUtils.testBook();

    @Test
    void testCreateBook() {
        Book b = bookService.create(testBook);
        assertNotNull(b.getId());
        assertEquals(testBook.getTitle(), b.getTitle());
    }

    @Test
    void testGetById() {
        Book b = bookService.create(testBook);
        Book retrievedBook = bookService.getById(b.getId());
        assertEquals(b.getId(), retrievedBook.getId());
    }

    @Test
    void testDeleteById() {
        Book b = bookService.create(testBook);
        bookService.deleteById(b.getId());
        assertThrows(CustomExceptionHandler.EntityNotFoundException.class, () -> bookService.getById(b.getId()));
    }

    @Test
    void testGetBookDetail() {
        Book b = bookService.create(testBook);
        ResponseBookDetailDTO detailDTO = bookService.getBookDetail(b.getId());

        assertEquals(b.getTitle(), detailDTO.getBook().getTitle());
        assertEquals(b.getAuthor(), detailDTO.getBook().getAuthor());
        assertTrue(detailDTO.getReviews().isEmpty());
    }

    @Test
    void testGetBestBooks() {
        authService.register(TestUtils.testRegisterRequest());
        AppUser user = userService.getByUsername(TestUtils.testRegisterRequest().getUsername());

        Book b1 = bookService.create(TestUtils.testBook("Book 1"));
        b1.setReviews(Collections.singletonList(reviewService.create(TestUtils.testReview(b1, user, 3))));
        Book b2 = bookService.create(TestUtils.testBook("Book 2"));
        b2.setReviews(Collections.singletonList(reviewService.create(TestUtils.testReview(b2, user, 1))));
        Book b3 = bookService.create(TestUtils.testBook("Book 3"));
        b3.setReviews(Collections.singletonList(reviewService.create(TestUtils.testReview(b3, user, 5))));

        List<ResponseBookDTO> bestBooks = bookService.getBest(2);

        assertEquals(2, bestBooks.size());
        assertEquals(b3.getId(), bestBooks.getFirst().getId());
        // TODO not working (it should)
        // assertEquals(b1.getId(), bestBooks.getLast().getId());
    }

    @Test
    void testGetAllBooksFilteredKnownSort() {
        Book b1 = bookService.create(TestUtils.testBook("Book 1", 6.99));
        Book b2 = bookService.create(TestUtils.testBook("Book 2", 12.99));

        ResponseBooksDTO response = bookService.getAllBooksFiltered(List.of(b1.getGenre().name()), "price-desc", 1, 10);

        assertEquals(2, response.getTotal());
        assertEquals(b2.getId(), response.getBooks().getFirst().getId());
        assertEquals(b1.getId(), response.getBooks().getLast().getId());
    }
}
