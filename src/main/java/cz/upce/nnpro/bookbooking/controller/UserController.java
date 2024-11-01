package cz.upce.nnpro.bookbooking.controller;

import cz.upce.nnpro.bookbooking.dto.UserNameDTO;
import cz.upce.nnpro.bookbooking.dto.UserPasswordDTO;
import cz.upce.nnpro.bookbooking.entity.User;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<User> getUserById(
            @RequestHeader("Authorization")
            String token) {
        final User user = service.getById(jwtService.extractUserId(token));
        if (user != null) return ResponseEntity.ok(user);
        return ResponseEntity.status(403).build();
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @RequestBody
            @Valid
            UserNameDTO data,
            @RequestHeader("Authorization")
            String token) {
        final User foundByEmail = service.getByEmail(data.getEmail());
        if (foundByEmail != null && !foundByEmail.getId().equals(jwtService.extractUserId(token))) {
            return new ResponseEntity<>(new Exception("EMAIL_EXISTS"), HttpStatus.CONFLICT);
        }
        final User user = service.getById(jwtService.extractUserId(token));
        if (user != null) {
            user.setFirstname(user.getFirstname());
            user.setLastname(user.getLastname());
            user.setEmail(user.getEmail());
            service.update(user);
            return ResponseEntity.ok(jwtService.generateToken(user));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> updateUserPassword(
            @RequestBody
            @Valid
            UserPasswordDTO data,
            @RequestHeader("Authorization")
            String token) {
        final User user = service.getById(jwtService.extractUserId(token));
        if (user != null) {
            if (passwordEncoder.matches(data.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(data.getPassword()));
                service.update(user);
                return ResponseEntity.ok().build();
            } else {
                return new ResponseEntity<>(new Exception("OLD_PASSWORD_INCORRECT"), HttpStatus.CONFLICT);
            }
        }
        return ResponseEntity.status(403).build();
    }
}
