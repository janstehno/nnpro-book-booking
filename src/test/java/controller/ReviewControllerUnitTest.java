package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.nnpro.bookbooking.controller.ReviewController;
import cz.upce.nnpro.bookbooking.dto.RequestBookReviewDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookReviewDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Review;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import utils.WithMockCustomUser;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ReviewControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private ReviewService reviewService;

    @Mock private BookService bookService;

    @InjectMocks private ReviewController reviewController;

    private AppUser user;
    private Book book;
    private Review review;
    private ResponseBookReviewDTO reviewDTO;
    private RequestBookReviewDTO requestReviewDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();

        user = new AppUser();

        book = new Book("Book Title", "Test Author", GenreE.ACTION, "Test Description", true, true, true, 5, 3, 49.99);
        book.setId(1L);

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setBook(book);
        review.setRating(5);
        review.setText("Excellent book!");
        review.setDate(LocalDate.now());

        reviewDTO = new ResponseBookReviewDTO(review);
        requestReviewDTO = new RequestBookReviewDTO(5, "Excellent book!");
    }

    @Test
    @WithMockCustomUser
    void testGetReview() throws Exception {
        when(reviewService.get(user, book.getId())).thenReturn(reviewDTO);

        mockMvc.perform(get("/books/{bookId}/review", book.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(review.getId()))
               .andExpect(jsonPath("$.firstname").value(user.getFirstname()))
               .andExpect(jsonPath("$.lastname").value(user.getLastname()))
               .andExpect(jsonPath("$.rating").value(review.getRating()))
               .andExpect(jsonPath("$.text").value(review.getText()))
               .andExpect(jsonPath("$.date[0]").value(review.getDate().getYear()))
               .andExpect(jsonPath("$.date[1]").value(review.getDate().getMonthValue()))
               .andExpect(jsonPath("$.date[2]").value(review.getDate().getDayOfMonth()));

        verify(reviewService, times(1)).get(user, book.getId());
    }

    @Test
    @WithMockCustomUser
    void testCreateReview() throws Exception {
        when(bookService.getById(book.getId())).thenReturn(book);
        when(reviewService.create(user, book, requestReviewDTO)).thenReturn(reviewDTO);

        mockMvc.perform(post("/books/{bookId}/review", book.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestReviewDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(review.getId()))
               .andExpect(jsonPath("$.firstname").value(user.getFirstname()))
               .andExpect(jsonPath("$.lastname").value(user.getLastname()))
               .andExpect(jsonPath("$.rating").value(review.getRating()))
               .andExpect(jsonPath("$.text").value(review.getText()))
               .andExpect(jsonPath("$.date[0]").value(review.getDate().getYear()))
               .andExpect(jsonPath("$.date[1]").value(review.getDate().getMonthValue()))
               .andExpect(jsonPath("$.date[2]").value(review.getDate().getDayOfMonth()));

        verify(reviewService, times(1)).create(user, book, requestReviewDTO);
    }

    @Test
    @WithMockCustomUser
    void testUpdateReview() throws Exception {
        when(reviewService.update(user, book.getId(), requestReviewDTO)).thenReturn(reviewDTO);

        mockMvc.perform(put("/books/{bookId}/review", book.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(requestReviewDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(review.getId()))
               .andExpect(jsonPath("$.firstname").value(user.getFirstname()))
               .andExpect(jsonPath("$.lastname").value(user.getLastname()))
               .andExpect(jsonPath("$.rating").value(review.getRating()))
               .andExpect(jsonPath("$.text").value(review.getText()))
               .andExpect(jsonPath("$.date[0]").value(review.getDate().getYear()))
               .andExpect(jsonPath("$.date[1]").value(review.getDate().getMonthValue()))
               .andExpect(jsonPath("$.date[2]").value(review.getDate().getDayOfMonth()));

        verify(reviewService, times(1)).update(user, book.getId(), requestReviewDTO);
    }

    @Test
    @WithMockCustomUser
    void testDeleteReview() throws Exception {
        doNothing().when(reviewService).delete(user, book.getId());

        mockMvc.perform(delete("/books/{bookId}/review", book.getId()))
               .andExpect(status().isOk());

        verify(reviewService, times(1)).delete(user, book.getId());
    }
}

