package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.repository.BookingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    private final List<StatusE> restrictedStatus = Arrays.asList(StatusE.WAITING, StatusE.UNCLAIMED, StatusE.RETURNED, StatusE.CANCELED);

    private final BookingRepository bookingRepository;

    public List<ResponseBookingDTO> getAllByUserId(Long userId) {
        return bookingRepository.findAllByOrderUserId(userId)
                                .stream()
                                .map(b -> ResponseBookingDTO.builder()
                                                            .book(b.getBook())
                                                            .count(b.getCount())
                                                            .status(b.getStatus())
                                                            .bookingDate(b.getBookingDate())
                                                            .expirationDate(b.getExpirationDate())
                                                            .build())
                                .toList();
    }

    public void updateReturnedBooks(Long userId, List<Long> bookIds) {
        List<Booking> bookings = bookingRepository.findAllByOrderUserIdAndBookIdIn(userId, bookIds);
        for (Booking booking : bookings) {
            if (booking.getStatus().equals(StatusE.AVAILABLE) || restrictedStatus.contains(booking.getStatus())) continue;
            booking.setStatus(StatusE.RETURNED);
        }
        bookingRepository.saveAll(bookings);
    }

    public void updateLoanedBooks(Long userId, List<Long> bookIds) {
        List<Booking> bookings = bookingRepository.findAllByOrderUserIdAndBookIdIn(userId, bookIds);
        for (Booking booking : bookings) {
            if (restrictedStatus.contains(booking.getStatus())) continue;
            booking.setStatus(StatusE.LOANED);
        }
        bookingRepository.saveAll(bookings);
    }
}
