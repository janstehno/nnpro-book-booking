package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.PurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService service;

    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases(
            @RequestHeader("Authorization")
            String token) {
        final Long userId = jwtService.extractUserId(token);
        final List<Purchase> purchases = service.getAllByUserId(userId);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(
            @PathVariable
            Long id,
            @RequestHeader("Authorization")
            String token) {
        final Long userId = jwtService.extractUserId(token);
        final Purchase purchase = service.getByIdAndUserId(id, userId);
        if (purchase != null) return ResponseEntity.ok(purchase);
        return ResponseEntity.notFound().build();
    }

}

