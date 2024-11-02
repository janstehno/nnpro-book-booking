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
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok().body(user);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @RequestBody
            @Valid
            UserNameDTO data,
            @RequestHeader("Authorization")
            String token) {
        final User user = service.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final User foundByEmail = service.getByEmail(data.getEmail());
        if (foundByEmail != null && !foundByEmail.getId().equals(jwtService.extractUserId(token))) {
            return new ResponseEntity<>(new Exception("EMAIL_EXISTS"), HttpStatus.CONFLICT);
        }
        final String newToken = jwtService.generateToken(service.update(user, data));
        return ResponseEntity.ok().body(newToken);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updateUserPassword(
            @RequestBody
            @Valid
            UserPasswordDTO data,
            @RequestHeader("Authorization")
            String token) {
        final User user = service.getById(jwtService.extractUserId(token));
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!passwordEncoder.matches(data.getOldPassword(), user.getPassword())) {
            return new ResponseEntity<>(new Exception("OLD_PASSWORD_INCORRECT"), HttpStatus.CONFLICT);
        }
        final String newToken = jwtService.generateToken(service.update(user, data, passwordEncoder));
        return ResponseEntity.ok().body(newToken);
    }
}
