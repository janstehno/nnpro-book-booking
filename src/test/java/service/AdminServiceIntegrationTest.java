package service;

import cz.upce.nnpro.bookbooking.Application;
import cz.upce.nnpro.bookbooking.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.dto.RequestOrderDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseOrderDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import utils.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class AdminServiceIntegrationTest {

    @Autowired private AdminService adminService;

    @Autowired private AuthService authService;

    @Autowired private UserService userService;

    @Autowired private BookingService bookingService;

    @Autowired private BookService bookService;

    @Autowired private OrderService orderService;

    private final RegisterRequest testRegisterRequest = TestUtils.testRegisterRequest();

    @Test
    void shouldReturnBookingsForUser() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());

        List<ResponseBookingDTO> bookings = adminService.getAllByUserId(user.getId());

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void shouldUpdateLoanedBooks() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());
        Book b1 = bookService.create(TestUtils.testBook("Book 1"));
        Book b2 = bookService.create(TestUtils.testBook("Book 2"));
        List<RequestOrderDTO> request = List.of(new RequestOrderDTO(b1.getId(), 2, false), new RequestOrderDTO(b2.getId(), 1, false));
        ResponseOrderDTO response = orderService.create(user, request);

        assertNotNull(response.getBookings());
        List<Long> bookingIds = response.getBookings().stream().map(ResponseBookingDTO::getId).toList();

        adminService.updateLoanedBooks(user.getId(), bookingIds);

        bookingIds.forEach(bookingId -> {
            Booking booking = bookingService.getById(bookingId);
            assertEquals(StatusE.LOANED, booking.getStatus());
        });
    }

    @Test
    void shouldUpdateReturnedBooks() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());
        Book b1 = bookService.create(TestUtils.testBook("Book 1"));
        Book b2 = bookService.create(TestUtils.testBook("Book 2"));
        List<RequestOrderDTO> request = List.of(new RequestOrderDTO(b1.getId(), 2, false), new RequestOrderDTO(b2.getId(), 1, false));
        ResponseOrderDTO response = orderService.create(user, request);

        assertNotNull(response.getBookings());
        List<Long> bookingIds = response.getBookings().stream().map(ResponseBookingDTO::getId).toList();


        adminService.updateLoanedBooks(user.getId(), bookingIds);
        adminService.updateReturnedBooks(user.getId(), bookingIds);

        bookingIds.forEach(bookingId -> {
            Booking booking = bookingService.getById(bookingId);
            assertEquals(StatusE.RETURNED, booking.getStatus());
        });
    }

    @Test
    void shouldNotUpdate_whenOnline(){
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());
        Book b = bookService.create(TestUtils.testBook("Book 1"));
        b.setOnline(true);
        List<RequestOrderDTO> request = List.of(new RequestOrderDTO(b.getId(), 0, true));
        ResponseOrderDTO response = orderService.create(user, request);

        assertNotNull(response.getBookings());
        List<Long> bookingIds = response.getBookings().stream().map(ResponseBookingDTO::getId).toList();

        adminService.updateLoanedBooks(user.getId(), bookingIds);

        Booking booking = bookingService.getById(bookingIds.getFirst());
        assertEquals(StatusE.ONLINE, booking.getStatus());
    }
}
