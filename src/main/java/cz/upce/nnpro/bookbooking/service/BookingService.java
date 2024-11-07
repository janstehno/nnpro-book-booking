package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.repository.BookingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService implements ServiceInterface<Booking> {

    private final BookingRepository bookingRepository;

    private final BookService bookService;

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id).orElse(null);
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

    public StatusE updateReturned(Booking booking) {
        booking.setStatus(StatusE.RETURNED);
        booking.setReturnDate(LocalDate.now());

        final Book book = booking.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + booking.getCount());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> waitingBookingsPage = bookingRepository.findByBookAndStatusOrderByOrderDateAsc(book, StatusE.WAITING, pageable);
        List<Booking> waitingBookings = waitingBookingsPage.getContent();

        for (Booking waitingBooking : waitingBookings) {
            if (book.getAvailableCopies() >= waitingBooking.getCount()) {
                waitingBooking.setStatus(StatusE.AVAILABLE);
                // TODO when to send the email about available books?
                book.setAvailableCopies(book.getAvailableCopies() - waitingBooking.getCount());
                bookingRepository.save(waitingBooking);
            } else break;
        }

        bookService.update(book);

        return bookingRepository.save(booking).getStatus();
    }

    public StatusE updateLoaned(Booking booking) {
        booking.setStatus(StatusE.LOANED);
        booking.setLoanDate(LocalDate.now());
        booking.setExpirationDate(LocalDate.now().plusDays(30));

        return bookingRepository.save(booking).getStatus();
    }
}
