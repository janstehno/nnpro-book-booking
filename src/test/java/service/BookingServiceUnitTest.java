package service;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.repository.BookingRepository;
import cz.upce.nnpro.bookbooking.service.BookingService;
import cz.upce.nnpro.bookbooking.service.MailService;
import cz.upce.nnpro.bookbooking.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    @Mock private BookingRepository bookingRepository;

    @Mock private MailService mailService;

    @Mock private BookService bookService;

    @InjectMocks private BookingService bookingService;

    private Booking booking1, booking2, booking3, booking4;
    private Book book1, book2;

    @BeforeEach
    void setUp() {
        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book A");
        book1.setAvailableCopies(10);

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book B");
        book2.setOnline(true);
        book2.setAvailableCopies(2);

        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBook(book1);
        booking1.setCount(2);
        booking1.setStatus(StatusE.AVAILABLE);
        booking1.setBookingDate(LocalDate.now());

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setBook(book2);
        booking2.setCount(3);
        booking2.setStatus(StatusE.WAITING);
        booking2.setBookingDate(LocalDate.now());

        booking3 = new Booking();
        booking3.setId(3L);
        booking3.setBook(book1);
        booking3.setCount(1);
        booking3.setStatus(StatusE.LOANED);
        booking3.setBookingDate(LocalDate.now());
        booking3.setLoanDate(LocalDate.now());
        booking3.setExpirationDate(LocalDate.now().plusDays(30));

        booking4 = new Booking();
        booking4.setId(4L);
        booking4.setBook(book2);
        booking4.setCount(0);
        booking4.setStatus(StatusE.ONLINE);
        booking4.setBookingDate(LocalDate.now());
        booking4.setLoanDate(LocalDate.now());
        booking4.setExpirationDate(LocalDate.now().plusDays(30));
    }

    @Test
    void testGetBookingById() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));

        Booking result = bookingService.getById(1L);

        assertNotNull(result);
        assertEquals(booking1, result);
    }

    @Test
    void testGetAllBookings() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking1, booking2, booking3));

        List<Booking> bookings = bookingService.getAll();

        assertEquals(3, bookings.size());
    }

    @Test
    void testCreateBooking() {
        when(bookingRepository.save(booking1)).thenReturn(booking1);

        Booking result = bookingService.create(booking1);

        assertNotNull(result);
        assertEquals(booking1, result);
    }

    @Test
    void testUpdateBooking() {
        when(bookingRepository.save(booking1)).thenReturn(booking1);

        Booking result = bookingService.update(booking1);

        assertNotNull(result);
        assertEquals(booking1, result);
    }

    @Test
    void testDeleteBookingById() {
        doNothing().when(bookingRepository).deleteById(1L);

        bookingService.deleteById(1L);

        verify(bookingRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCreateAllBookings() {
        Set<Booking> bookings = new HashSet<>(List.of(booking1, booking2));

        when(bookingRepository.saveAll(bookings)).thenReturn(new ArrayList<>(bookings));

        List<Booking> result = bookingService.createAll(bookings);

        assertEquals(2, result.size());
    }

    @Test
    void testUpdateReturnedBooking() {
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        when(bookService.update(book1)).thenReturn(book1);

        when(bookingRepository.findByBookAndStatusOrderByOrderDateAsc(any(Book.class),
                                                                      eq(StatusE.WAITING),
                                                                      any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.updateReturned(booking1);

        assertEquals(StatusE.RETURNED, booking1.getStatus());
        assertNotNull(booking1.getReturnDate());
    }

    @Test
    void testUpdateLoanedBooking() {
        when(bookingRepository.save(booking1)).thenReturn(booking1);

        bookingService.updateLoaned(booking1);

        assertEquals(StatusE.LOANED, booking1.getStatus());
        assertNotNull(booking1.getLoanDate());
        assertNotNull(booking1.getExpirationDate());
    }

    @Test
    void testCancelBooking() {
        when(bookingRepository.findByOrderUserIdAndOrderIdAndId(1L, 1L, 1L)).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);

        bookingService.cancelBooking(1L, 1L, 1L);

        assertEquals(StatusE.CANCELED, booking1.getStatus());
    }

    @Test
    void testNotCancelBooking_WhenOnline() {
        when(bookingRepository.findByOrderUserIdAndOrderIdAndId(1L, 1L, 4L)).thenReturn(Optional.of(booking4));

        bookingService.cancelBooking(1L, 1L, 4L);

        assertEquals(StatusE.ONLINE, booking4.getStatus());
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testReserveBooks_ShouldReserve() {
        when(bookingRepository.findByBookAndStatusOrderByOrderDateAsc(book1,
                                                                      StatusE.WAITING,
                                                                      PageRequest.of(0, 10))).thenReturn(new PageImpl<>(Collections.singletonList(booking2),
                                                                                                                        PageRequest.of(0, 10),
                                                                                                                        1));
        when(bookService.update(book1)).thenReturn(book1);

        bookingService.reserveBookForNextInLine(book1);

        verify(mailService, times(1)).sendEmailAboutAvailableReservedBook(booking2);
    }

    @Test
    void testReserveBooks_ShouldNotReserve_WhenNotEnoughCopies() {
        when(bookingRepository.findByBookAndStatusOrderByOrderDateAsc(book2,
                                                                      StatusE.WAITING,
                                                                      PageRequest.of(0, 10))).thenReturn(new PageImpl<>(Collections.singletonList(booking2),
                                                                                                                        PageRequest.of(0, 10),
                                                                                                                        1));
        when(bookService.update(book2)).thenReturn(book2);

        bookingService.reserveBookForNextInLine(book2);

        verify(mailService, never()).sendEmailAboutAvailableReservedBook(booking2);
    }
}
