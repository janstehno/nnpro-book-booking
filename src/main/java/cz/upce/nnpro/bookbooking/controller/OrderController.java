package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.RequestOrderDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseOrderDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.OrderService;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    private final UserService userService;

    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<ResponseOrderDTO>> getAllOrders(
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.getAllByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseOrderDTO> getOrderById(
            @PathVariable
            Long id,
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.getByIdAndUserId(id, user.getId()));
    }

    @PostMapping("/new")
    public ResponseEntity<ResponseOrderDTO> createOrder(
            @RequestBody
            @Valid
            RequestOrderDTO data,
            @AuthenticationPrincipal
            AppUser user) {
        //TODO lock?
        return ResponseEntity.ok(service.create(user, data));
    }

}
