package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.RequestPurchaseDTO;
import cz.upce.nnpro.bookbooking.dto.ResponsePurchaseDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService service;

    @GetMapping
    public ResponseEntity<List<ResponsePurchaseDTO>> getAllPurchases(
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.getAllByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsePurchaseDTO> getPurchaseById(
            @PathVariable
            Long id,
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.getByIdAndUserId(id, user.getId()));
    }

    @PostMapping("/new")
    public ResponseEntity<ResponsePurchaseDTO> createPurchase(
            @RequestBody
            @Valid
            List<RequestPurchaseDTO> purchases,
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.create(user, purchases));
    }

}
