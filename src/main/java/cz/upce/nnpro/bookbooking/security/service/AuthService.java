package cz.upce.nnpro.bookbooking.security.service;

import cz.upce.nnpro.bookbooking.entity.User;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.security.dto.AuthenticationResponse;
import cz.upce.nnpro.bookbooking.security.dto.LoginRequest;
import cz.upce.nnpro.bookbooking.security.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.security.jwt.JwtService;
import cz.upce.nnpro.bookbooking.service.RoleService;
import cz.upce.nnpro.bookbooking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    private final UserService userService;

    private final RoleService roleService;

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
                              .creation_date(LocalDateTime.now())
                              .update_date(LocalDateTime.now())
                              .roles(Set.of(roleService.getByName(RoleE.USER)))
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
