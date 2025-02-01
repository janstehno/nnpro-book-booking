package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.BookingRepository;
import cz.upce.nnpro.bookbooking.security.RedisService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class BookingService implements ServiceInterface<Booking> {

    private static final int AVAILABILITY_DAYS = 3;
    private static final int RESERVATION_VALIDITY_DAYS = 30;

    private final MailService mailService;
    private final BookingRepository bookingRepository;
    private final BookService bookService;
    private final RedisService redisService;

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
        RLock lock = redisService.getLock("update-returned-lock-" + booking.getId());
        try {
            if (!redisService.tryLock(lock, 5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Could not acquire lock for updateReturned.");
            }

            booking.setStatus(StatusE.RETURNED);
            booking.setReturnDate(LocalDate.now());

            final Book book = booking.getBook();
            freeBooks(book, booking.getCount());

            reserveBookForNextInLine(book);
            bookingRepository.save(booking);
        } finally {
            redisService.releaseLock(lock);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void checkOnlineBookingExpiration() {
        checkExpirationFor(StatusE.AVAILABLE, StatusE.UNCLAIMED);
        checkExpirationFor(StatusE.ONLINE, StatusE.RETURNED);
    }

    @Transactional
    public void reserveBookForNextInLine(Book book) {
        RLock lock = redisService.getLock("reserve-book-for-next-in-line-lock-" + book.getId());
        try {
            if (!redisService.tryLock(lock, 5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Could not acquire lock for reserveBookForNextInLine.");
            }

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
        } finally {
            redisService.releaseLock(lock);
        }
    }

    @Transactional
    public void updateLoaned(Booking booking) {
        RLock lock = redisService.getLock("update-loaned-lock-" + booking.getId());
        try {
            if (!redisService.tryLock(lock, 5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Could not acquire lock for updateLoaned.");
            }

            booking.setStatus(StatusE.LOANED);
            booking.setLoanDate(LocalDate.now());
            booking.setExpirationDate(LocalDate.now().plusDays(RESERVATION_VALIDITY_DAYS));
            update(booking);
        } finally {
            redisService.releaseLock(lock);
        }
    }

    @Transactional
    public void handleReservation(Booking booking) {
        RLock lock = redisService.getLock("handle-reservation-lock-" + booking.getId());
        try {
            if (!redisService.tryLock(lock, 5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Could not acquire lock for handleReservation.");
            }

            if (booking.isOnline()) setOnlineBookingAvailable(booking);
            else if (reserveBooks(booking.getBook(), booking.getCount())) setBookingAvailable(booking);
            else booking.setStatus(StatusE.WAITING);
        } finally {
            redisService.releaseLock(lock);
        }
    }

    @Transactional
    public void cancelBooking(Long userId, Long orderId, Long bookingId) {
        RLock lock = redisService.getLock("cancel-booking-lock-" + bookingId);
        try {
            if (!redisService.tryLock(lock, 5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Could not acquire lock for cancelBooking.");
            }

            Booking booking = getByOrderUserIdAndOrderIdAndId(userId, orderId, bookingId);

            if (booking.getStatus().equals(StatusE.ONLINE) || booking.isOnline() || booking.getBook().isOnline())
                return;
            freeBooks(booking.getBook(), booking.getCount());
            booking.setStatus(StatusE.CANCELED);
            update(booking);
        } finally {
            redisService.releaseLock(lock);
        }
    }

    @Transactional
    private void checkExpirationFor(StatusE status, StatusE target) {
        LocalDate today = LocalDate.now();

        RLock lock = redisService.getLock("check-expiration-for-lock-" + status.name());
        try {
            if (!redisService.tryLock(lock, 5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Could not acquire lock for checkExpirationFor.");
            }

            Pageable pageable = PageRequest.of(0, 10);
            Page<Booking> availableBookingsPage = bookingRepository.findByStatus(status, pageable);
            List<Booking> availableBookings = availableBookingsPage.getContent();

            for (Booking booking : availableBookings) {
                if (booking.getExpirationDate() != null && booking.getExpirationDate().isAfter(today)) {
                    if (booking.getStatus().equals(StatusE.ONLINE)) freeBooks(booking.getBook(), booking.getCount());
                    booking.setStatus(target);
                    update(booking);
                }
            }
        } finally {
            redisService.releaseLock(lock);
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

    protected boolean reserveBooks(Book book, int count) {
        if (book.getAvailableCopies() >= count) {
            book.setAvailableCopies(book.getAvailableCopies() - count);
            bookService.update(book);
            return true;
        }
        return false;
    }

    protected void freeBooks(Book book, int count) {
        book.setAvailableCopies(book.getAvailableCopies() + count);
        bookService.update(book);
    }
}
