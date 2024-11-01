package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestHeader("Authorization")
            String token) {
        final Long userId = jwtService.extractUserId(token);
        final List<Order> orders = service.getAllByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable
            Long id,
            @RequestHeader("Authorization")
            String token) {
        final Long userId = jwtService.extractUserId(token);
        final Order order = service.getByIdAndUserId(id, userId);
        if (order != null) return ResponseEntity.ok(order);
        return ResponseEntity.notFound().build();
    }

}

