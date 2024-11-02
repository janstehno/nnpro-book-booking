package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.PurchaseDTO;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.entity.User;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.PurchaseService;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService service;

    private final UserService userService;

    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases(
            @RequestHeader("Authorization")
            String token) {
        final User user = userService.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final List<Purchase> purchases = service.getAllByUserId(user.getId());
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(
            @PathVariable
            Long id,
            @RequestHeader("Authorization")
            String token) {
        final User user = userService.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final Purchase purchase = service.getByIdAndUserId(id, user.getId());
        if (purchase == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(purchase);
    }

    @PostMapping("/new")
    public ResponseEntity<Purchase> createPurchase(
            @RequestBody
            @Valid
            PurchaseDTO data,
            @RequestHeader("Authorization")
            String token) {
        final User user = userService.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final Purchase purchase = service.create(user, data);
        return ResponseEntity.ok(purchase);
    }

}
