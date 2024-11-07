package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Booking>> getAllBookingsOfUser(
            @PathVariable
            Long userId) {
        final List<Booking> bookings = service.getAllByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{userId}/{id}/returned")
    public ResponseEntity<StatusE> updateReturnedBooking(
            @PathVariable
            Long id) {
        final Booking booking = service.getById(id);
        if (booking == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        // TODO lock?
        final StatusE status = service.updateReturned(booking);
        return ResponseEntity.ok(status);
    }

    @PutMapping("/{userId}/{id}/loaned")
    public ResponseEntity<StatusE> updateLoanedBooking(
            @PathVariable
            Long id) {
        final Booking booking = service.getById(id);
        if (booking == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        final StatusE status = service.updateLoaned(booking);
        return ResponseEntity.ok(status);
    }

}
