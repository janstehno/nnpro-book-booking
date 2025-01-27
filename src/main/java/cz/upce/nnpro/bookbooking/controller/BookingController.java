package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders/{orderId}/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBookingById(
            @AuthenticationPrincipal
            AppUser user,
            @PathVariable
            Long orderId,
            @PathVariable
            Long bookingId) {
        service.cancelBooking(user.getId(), orderId, bookingId);
        return ResponseEntity.ok().build();
    }
}
