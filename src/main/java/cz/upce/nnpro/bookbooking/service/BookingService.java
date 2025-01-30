package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class BookingService implements ServiceInterface<Booking> {

    private static final int AVAILABILITY_DAYS = 3;

    private static final int RESERVATION_VALIDITY_DAYS = 30;
    private final MailService mailService;

    private final BookingRepository bookingRepository;

    private final BookService bookService;

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getById(Long id) throws RuntimeException {
        return bookingRepository.findById(id).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }

    @Override
    public Booking create(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<Booking> createAll(Set<Booking> bookings) {
        return bookingRepository.saveAll(bookings);
    }

    public List<Booking> getAllByUserId(Long userId) {
        return bookingRepository.findAllByOrderUserId(userId);
    }

    public List<Booking> getAllByOrderUserId(Long userId) {
        return bookingRepository.findAllByOrderUserId(userId);
    }

    public List<Booking> getAllByOrderUserIdAndIdIn(Long userId, List<Long> bookIds) {
        return bookingRepository.findAllByOrderUserIdAndIdIn(userId, bookIds);
    }

    public Booking getByOrderUserIdAndOrderIdAndId(Long userId, Long orderId, Long bookingId) throws RuntimeException {
        return bookingRepository.findByOrderUserIdAndOrderIdAndId(userId, orderId, bookingId)
                                .orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }

    @Transactional
    public void updateReturned(Booking booking) {
        booking.setStatus(StatusE.RETURNED);
        booking.setReturnDate(LocalDate.now());

        final Book book = booking.getBook();
        freeBooks(book, booking.getCount());

        reserveBookForNextInLine(book);
        bookingRepository.save(booking);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void checkOnlineBookingExpiration() {
        checkExpirationFor(StatusE.AVAILABLE, StatusE.UNCLAIMED);
        checkExpirationFor(StatusE.ONLINE, StatusE.RETURNED);
    }

    @Transactional
    public void reserveBookForNextInLine(Book book) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> waitingBookingsPage = bookingRepository.findByBookAndStatusOrderByOrderDateAsc(book, StatusE.WAITING, pageable);
        List<Booking> waitingBookings = waitingBookingsPage.getContent();

        for (Booking waitingBooking : waitingBookings) {
            if (reserveBooks(book, waitingBooking.getCount())) {
                setBookingAvailable(waitingBooking);
                update(waitingBooking);
                mailService.sendEmailAboutAvailableReservedBook(waitingBooking);
            } else break;
        }

        bookService.update(book);
    }

    @Transactional
    public void updateLoaned(Booking booking) {
        booking.setStatus(StatusE.LOANED);
        booking.setLoanDate(LocalDate.now());
        booking.setExpirationDate(LocalDate.now().plusDays(RESERVATION_VALIDITY_DAYS));
        update(booking);
    }

    @Transactional
    public void handleReservation(Booking booking) {
        if (booking.isOnline()) setOnlineBookingAvailable(booking);
        else if (reserveBooks(booking.getBook(), booking.getCount())) setBookingAvailable(booking);
        else booking.setStatus(StatusE.WAITING);
    }

    @Transactional
    public void cancelBooking(Long userId, Long orderId, Long bookingId) {
        Booking booking = getByOrderUserIdAndOrderIdAndId(userId, orderId, bookingId);

        if (booking.isOnline()) return;
        freeBooks(booking.getBook(), booking.getCount());
        booking.setStatus(StatusE.CANCELED);
        update(booking);
    }

    private void checkExpirationFor(StatusE status, StatusE target) {
        LocalDate today = LocalDate.now();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> availableBookingsPage = bookingRepository.findByStatus(status, pageable);
        List<Booking> availableBookings = availableBookingsPage.getContent();

        for (Booking booking : availableBookings) {
            if (booking.getExpirationDate().isAfter(today)) {
                booking.setStatus(target);
                bookingRepository.save(booking);
            }
        }
    }

    private void setOnlineBookingAvailable(Booking booking) {
        booking.setStatus(StatusE.ONLINE);
        booking.setLoanDate(LocalDate.now());
        booking.setExpirationDate(LocalDate.now().plusDays(RESERVATION_VALIDITY_DAYS));
        update(booking);
    }

    private void setBookingAvailable(Booking booking) {
        booking.setStatus(StatusE.AVAILABLE);
        booking.setExpirationDate(LocalDate.now().plusDays(AVAILABILITY_DAYS));
    }

    @Transactional
    protected boolean reserveBooks(Book book, int count) {
        if (book.getAvailableCopies() >= count) {
            book.setAvailableCopies(book.getAvailableCopies() - count);
            bookService.update(book);
            return true;
        }
        return false;
    }

    @Transactional
    protected void freeBooks(Book book, int count) {
        book.setAvailableCopies(book.getAvailableCopies() + count);
        bookService.update(book);
    }
}
