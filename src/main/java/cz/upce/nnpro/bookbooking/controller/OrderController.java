package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.OrderDTO;
import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.entity.User;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.OrderService;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    private final BookService bookService;

    private final UserService userService;

    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestHeader("Authorization")
            String token) {
        final User user = userService.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final List<Order> orders = service.getAllByUserId(user.getId());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable
            Long id,
            @RequestHeader("Authorization")
            String token) {
        final User user = userService.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final Order order = service.getByIdAndUserId(id, user.getId());
        if (order == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(order);
    }

    @PostMapping("/new")
    public ResponseEntity<Order> createOrder(
            @RequestBody
            @Valid
            OrderDTO data,
            @RequestHeader("Authorization")
            String token) {
        final User user = userService.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        //TODO lock
        final Order order = service.create(user, data);
        //TODO unlock
        return ResponseEntity.ok(order);
    }

}
