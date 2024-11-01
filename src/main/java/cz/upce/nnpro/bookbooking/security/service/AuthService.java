package cz.upce.nnpro.bookbooking.security.service;

import cz.upce.nnpro.bookbooking.entity.ResetToken;
import cz.upce.nnpro.bookbooking.entity.User;
import cz.upce.nnpro.bookbooking.security.dto.*;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    private final UserService userService;

    private final ResetTokenService resetTokenService;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> register(RegisterRequest registerRequest) {
        final User foundByEmail = userService.getByEmail(registerRequest.getEmail());
        if (foundByEmail != null) {
            return new ResponseEntity<>(new Exception("EMAIL_EXISTS"), HttpStatus.CONFLICT);
        }
        final User foundByUsername = userService.getByUsername(registerRequest.getUsername());
        if (foundByUsername != null) {
            return new ResponseEntity<>(new Exception("USERNAME_EXISTS"), HttpStatus.CONFLICT);
        }
        final User user = User.builder()
                              .firstname(registerRequest.getFirstname())
                              .lastname(registerRequest.getLastname())
                              .email(registerRequest.getEmail())
                              .username(registerRequest.getUsername())
                              .password(passwordEncoder.encode(registerRequest.getPassword()))
                              .build();
        userService.create(user);
        final User found = userService.getByUsername(registerRequest.getUsername());
        return returnAuthenticatedUser(found);
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        final User found = userService.getByUsername(loginRequest.getUsername());
        if (found != null && passwordEncoder.matches(loginRequest.getPassword(), found.getPassword())) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            } catch (AuthenticationException e) {
                return new ResponseEntity<>(new Exception("WRONG_PASSWORD"), HttpStatus.FORBIDDEN);
            }
            return returnAuthenticatedUser(found);
        } else {
            return new ResponseEntity<>(new Exception("USERNAME_NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> passwordMail(PasswordResetRequest passwordResetRequest, HttpServletRequest httpRequest) {
        String token = null;
        User user = userService.getByUsername(passwordResetRequest.getUsername());

        if (user != null) {
            token = jwtService.generateResetToken(user);
            ResetToken resetToken = ResetToken.builder().token(token).user(user).expiration(LocalDateTime.now().plusMinutes(15)).build();
            resetTokenService.create(resetToken);

            String appUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName() + ":" + httpRequest.getServerPort();
            String resetUrl = appUrl + "/auth/password/new?token=" + token;

            mailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        }

        return ResponseEntity.ok(token);
    }

    public ResponseEntity<?> passwordReset(PasswordResetDTO passwordResetDTO) {
        if (passwordResetDTO.getNewPassword().equals(passwordResetDTO.getConfirmPassword())) {
            String token = passwordResetDTO.getToken();
            String username = jwtService.extractUsername(token);
            User user = userService.getByUsername(username);

            if (user != null) {
                if (username == null || !jwtService.isTokenValid(token, user)) {
                    return ResponseEntity.status(400).body("INVALID_TOKEN");
                }

                String purpose = jwtService.extractClaim(token, claims -> (String) claims.get("usedFor"));
                if (!"PASSWORD_RESET".equals(purpose)) {
                    return ResponseEntity.status(400).body("INVALID_TOKEN");
                }

                user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
                userService.update(user);

                ResetToken resetToken = resetTokenService.getByToken(token);
                if (resetToken != null) resetTokenService.deleteById(resetToken.getId());

                return ResponseEntity.ok("PASSWORD_RESET");

            }
        }

        return ResponseEntity.status(400).body("PASSWORD_NOT_RESET");
    }

    private ResponseEntity<?> returnAuthenticatedUser(User user) {
        final String token = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                                                       .id(user.getId())
                                                       .firstname(user.getFirstname())
                                                       .lastname(user.getLastname())
                                                       .token(token)
                                                       .build());
    }
}
