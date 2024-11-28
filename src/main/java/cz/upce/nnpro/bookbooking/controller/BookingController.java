package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    @GetMapping("/manager/{userId}")
    public ResponseEntity<List<Booking>> getAllBookingsOfUser(
            @PathVariable
            Long userId) {
        return ResponseEntity.ok(service.getAllByUserId(userId));
    }

    @PutMapping("/manager/{userId}/{id}/returned")
    public ResponseEntity<StatusE> updateReturnedBooking(
            @PathVariable
            Long id) {
        return ResponseEntity.ok(service.updateReturned(id));
    }

    @PutMapping("/manager/{userId}/{id}/loaned")
    public ResponseEntity<StatusE> updateLoanedBooking(
            @PathVariable
            Long id) {
        return ResponseEntity.ok(service.updateLoaned(id));
    }

}
