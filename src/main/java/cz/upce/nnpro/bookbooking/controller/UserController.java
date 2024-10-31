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
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable
            Long id,
            @RequestHeader("Authorization")
            String token) {
        if (modifiable(token, id)) {
            final User user = service.getById(id);
            if (user != null) return ResponseEntity.ok(user);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            UserNameDTO data,
            @RequestHeader("Authorization")
            String token) {
        final User foundByEmail = service.getByEmail(data.getEmail());
        if (foundByEmail != null && !foundByEmail.getId().equals(id)) {
            return new ResponseEntity<>(new Exception("EMAIL_EXISTS"), HttpStatus.CONFLICT);
        }
        if (modifiable(token, id)) {
            final User user = service.getById(id);
            if (user != null) {
                user.setFirstname(user.getFirstname());
                user.setLastname(user.getLastname());
                user.setEmail(user.getEmail());
                user.setUpdate_date(user.getUpdate_date());
                service.update(user);
                return ResponseEntity.ok(jwtService.generateToken(user));
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> updateUserPassword(
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            UserPasswordDTO data,
            @RequestHeader("Authorization")
            String token) {
        if (modifiable(token, id)) {
            final User user = service.getById(id);
            if (user != null) {
                if (passwordEncoder.matches(data.getOldPassword(), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(data.getPassword()));
                    user.setUpdate_date(data.getUpdateDate());
                    service.update(user);
                    return ResponseEntity.ok().build();
                } else {
                    return new ResponseEntity<>(new Exception("OLD_PASSWORD_INCORRECT"), HttpStatus.CONFLICT);
                }
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(403).build();
    }

    private boolean modifiable(String token, Long userId) {
        return userId.equals(jwtService.extractUserId(token.substring(7)));
    }

}

