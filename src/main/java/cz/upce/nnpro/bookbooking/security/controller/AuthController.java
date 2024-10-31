package cz.upce.nnpro.bookbooking.security.controller;

import cz.upce.nnpro.bookbooking.security.dto.LoginRequest;
import cz.upce.nnpro.bookbooking.security.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.security.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody
            RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody
            LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
