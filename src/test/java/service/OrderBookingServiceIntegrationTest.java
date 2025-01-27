package service;

import cz.upce.nnpro.bookbooking.Application;
import cz.upce.nnpro.bookbooking.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.dto.RequestOrderDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseOrderDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import utils.TestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class OrderBookingServiceIntegrationTest {

    @Autowired private BookingService bookingService;

    @Autowired private BookService bookService;

    @Autowired private OrderService orderService;

    @Autowired private AuthService authService;

    @Autowired private UserService userService;

    private final RegisterRequest testRegisterRequest = TestUtils.testRegisterRequest();

    private final Order testOrder() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());
        Book b1 = bookService.create(TestUtils.testBook("Book 1"));
        Book b2 = bookService.create(TestUtils.testBook("Book 2"));
        RequestOrderDTO request = new RequestOrderDTO(Map.of(b1.getId(), 2, b2.getId(), 1));
        ResponseOrderDTO response = orderService.create(user, request);
        assertNotNull(response.getBookings());
        return orderService.getById(response.getId());
    }

    @Test
    void shouldCreateBookings_WhenCreatingOrder() {
        Order order = testOrder();
        Booking booking = bookingService.getById(order.getBookings().getFirst().getId());

        assertNotNull(booking);
        assertEquals(order.getBookings().getFirst().getId(), booking.getId());
    }

    @Test
    void shouldGetBookingById() {
        Order order = testOrder();
        Booking booking = bookingService.getById(order.getBookings().getFirst().getId());

        assertNotNull(booking);
        assertEquals(booking.getId(), order.getBookings().getFirst().getId());
    }

    @Test
    void shouldGetBookingsByUserId() {
        Order order = testOrder();
        List<Booking> bookings = bookingService.getAllByUserId(order.getUser().getId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void shouldUpdateLoanedBooking() {
        Order order = testOrder();
        Booking booking = order.getBookings().getFirst();

        Booking tested = bookingService.getById(booking.getId());
        bookingService.updateLoaned(tested);

        assertEquals(StatusE.LOANED, tested.getStatus());
    }

    @Test
    void shouldUpdateReturnedBooking() {
        Order order = testOrder();
        Booking booking = order.getBookings().getFirst();

        Booking tested = bookingService.getById(booking.getId());
        tested.setStatus(StatusE.LOANED);
        bookingService.updateReturned(tested);

        assertEquals(StatusE.RETURNED, tested.getStatus());
    }

    @Test
    void shouldCancelBooking() {
        Order order = testOrder();
        Booking booking = order.getBookings().getFirst();

        bookingService.cancelBooking(order.getUser().getId(), order.getId(), booking.getId());

        assertEquals(StatusE.CANCELED, booking.getStatus());
    }

    @Test
    void shouldNotCancelBooking_WhenLoaned() {
        Order order = testOrder();
        Booking booking = order.getBookings().getFirst();
        booking.setStatus(StatusE.LOANED);

        bookingService.cancelBooking(order.getUser().getId(), order.getId(), booking.getId());

        assertEquals(StatusE.CANCELED, booking.getStatus());
    }
}
