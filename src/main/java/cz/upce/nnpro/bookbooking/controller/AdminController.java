package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.RequestBookingsDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.service.AdminService;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service;

    private final UserService userService;

    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<List<ResponseBookingDTO>> getAllBookingsOfUser(
            @PathVariable
            Long userId) {
        return ResponseEntity.ok(service.getAllByUserId(userId));
    }

    @PutMapping("/users/{userId}/bookings")
    public ResponseEntity<List<ResponseBookingDTO>> updateBookings(
            @PathVariable
            Long userId,
            @Valid
            @RequestBody
            RequestBookingsDTO requestBookingsDTO) {
        service.updateReturnedBooks(userId, requestBookingsDTO.getReturningBookIds());
        service.updateLoanedBooks(userId, requestBookingsDTO.getLoaningBookIds());
        return getAllBookingsOfUser(userId);
    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

}
