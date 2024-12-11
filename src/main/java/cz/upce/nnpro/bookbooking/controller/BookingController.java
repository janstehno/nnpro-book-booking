package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

}
