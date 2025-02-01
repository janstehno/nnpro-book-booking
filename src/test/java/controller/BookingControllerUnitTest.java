package controller;

import cz.upce.nnpro.bookbooking.controller.BookingController;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.BookingService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BookingControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private BookService bookService;

    @Mock private BookingService bookingService;

    @InjectMocks private BookingController bookingController;

    private Booking booking;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book A");
        book.setAvailableCopies(10);

        booking = new Booking();
        booking.setId(1L);
        booking.setBook(book);
        booking.setCount(2);
        booking.setStatus(StatusE.AVAILABLE);
        booking.setBookingDate(LocalDate.now());
    }

    @Test
    @WithMockCustomUser
    void cancelBookingById_ShouldReturnOk() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(post("/orders/{orderId}/bookings/{bookingId}/cancel", orderId, booking.getId()).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        verify(bookingService).cancelBooking(null, orderId, booking.getId());
    }
}
