package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.RequestUserPasswordDTO;
import cz.upce.nnpro.bookbooking.dto.UserDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<UserDTO> getUser(
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.get(user));
    }

    @PutMapping
    public ResponseEntity<String> updateUser(
            @RequestBody
            @Valid
            UserDTO data,
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.update(user, data));
    }

    @PutMapping("/password")
    public ResponseEntity<String> updateUserPassword(
            @RequestBody
            @Valid
            RequestUserPasswordDTO data,
            @AuthenticationPrincipal
            AppUser user) {
        return ResponseEntity.ok(service.updatePassword(user, data));
    }
}
