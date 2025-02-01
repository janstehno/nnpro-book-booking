package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    private final List<StatusE> restrictedStatus = List.of(StatusE.WAITING, StatusE.UNCLAIMED, StatusE.RETURNED, StatusE.CANCELED, StatusE.ONLINE);

    private final BookingService bookingService;

    public List<ResponseBookingDTO> getAllByUserId(Long userId) {
        return bookingService.getAllByOrderUserId(userId)
                             .stream()
                             .map(ResponseBookingDTO::new)
                             .toList();
    }

    @Transactional
    public void updateReturnedBooks(Long userId, List<Long> bookingIds) {
        if (bookingIds == null || bookingIds.isEmpty()) return;
        List<Booking> bookings = bookingService.getAllByOrderUserIdAndIdIn(userId, bookingIds);
        for (Booking booking : bookings) {
            if (booking.getStatus().equals(StatusE.AVAILABLE) || restrictedStatus.contains(booking.getStatus())) continue;
            bookingService.updateReturned(booking);
        }
    }

    @Transactional
    public void updateLoanedBooks(Long userId, List<Long> bookingIds) {
        if (bookingIds == null || bookingIds.isEmpty()) return;
        List<Booking> bookings = bookingService.getAllByOrderUserIdAndIdIn(userId, bookingIds);
        for (Booking booking : bookings) {
            if (booking.getStatus().equals(StatusE.LOANED) || restrictedStatus.contains(booking.getStatus())) continue;
            bookingService.updateLoaned(booking);
        }
    }
}
