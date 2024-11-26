package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.repository.BookingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService implements ServiceInterface<Booking> {

    private static final int AVAILABILITY_DAYS = 3;

    private static final int RESERVATION_VALIDITY_DAYS = 30;
    private final MailService mailService;

    @PersistenceContext
    private EntityManager entityManager;

    private final BookingRepository bookingRepository;

    private final BookService bookService;

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getById(Long id) throws RuntimeException {
        return bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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

    public List<Booking> getAllByUserId(Long userId) {
        return bookingRepository.findAllByOrderUserId(userId);
    }

    public StatusE updateReturned(Long bookingId) {
        final Booking booking = getById(bookingId);

        booking.setStatus(StatusE.RETURNED);
        booking.setReturnDate(LocalDate.now());

        final Book book = booking.getBook();
        freeBooks(book, booking.getCount());

        reserveBookForNextInLine(book);

        return bookingRepository.save(booking).getStatus();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void checkAvailableBookingExpiration() {
        var today = LocalDate.now();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> availableBookingsPage = bookingRepository.findByStatus(StatusE.AVAILABLE, pageable);
        List<Booking> availableBookings = availableBookingsPage.getContent();

        for (Booking booking : availableBookings) {
            if (booking.getExpirationDate().isAfter(today)) {
                booking.setStatus(StatusE.UNCLAIMED);
                bookingRepository.save(booking);

                reserveBookForNextInLine(booking.getBook());
            }
        }
    }

    public void reserveBookForNextInLine(Book book) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> waitingBookingsPage = bookingRepository.findByBookAndStatusOrderByOrderDateAsc(book, StatusE.WAITING, pageable);
        List<Booking> waitingBookings = waitingBookingsPage.getContent();

        for (Booking waitingBooking : waitingBookings) {
            if (reserveBooks(book.getId(), waitingBooking.getCount())) {
                setBookingAvailable(waitingBooking);
                mailService.sendEmailAboutAvailableReservedBook(waitingBooking);
            } else break;
        }

        bookService.update(book);
    }

    public StatusE updateLoaned(Long bookingId) {
        final Booking booking = getById(bookingId);

        booking.setStatus(StatusE.LOANED);
        booking.setLoanDate(LocalDate.now());
        booking.setExpirationDate(LocalDate.now().plusDays(RESERVATION_VALIDITY_DAYS));

        return bookingRepository.save(booking).getStatus();
    }

    public void handleReservation(Long bookId, int count, Booking booking) {
        if (reserveBooks(bookId, count)) {
            setBookingAvailable(booking);
        } else {
            booking.setStatus(StatusE.WAITING);
            bookingRepository.save(booking);
        }
    }

    private void setBookingAvailable(Booking booking) {
        booking.setStatus(StatusE.AVAILABLE);
        booking.setExpirationDate(LocalDate.now().plusDays(AVAILABILITY_DAYS));
        bookingRepository.save(booking);
    }

    private boolean reserveBooks(Long bookId, int count) {
        try {
            entityManager.getTransaction().begin();

            Book book = entityManager.find(Book.class, bookId, LockModeType.PESSIMISTIC_WRITE);

            if (book.getAvailableCopies() >= count) {
                book.setAvailableCopies(book.getAvailableCopies() - count);
                entityManager.getTransaction().commit();
                return true;
            }

            entityManager.getTransaction().rollback();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }

        return false;
    }

    private void freeBooks(Book book, int count) {
        // TODO critical section
        book.setAvailableCopies(book.getAvailableCopies() + count);
        bookService.update(book);
    }
}
