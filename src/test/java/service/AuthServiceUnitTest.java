package service;

import cz.upce.nnpro.bookbooking.dto.*;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.ResetToken;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.security.JwtService;
import cz.upce.nnpro.bookbooking.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {

    @Mock private JwtService jwtService;

    @Mock private UserService userService;

    @Mock private RoleService roleService;

    @Mock private ResetTokenService resetTokenService;

    @Mock private MailService mailService;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private AppUser user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new AppUser("John", "Doe", "john.doe@example.com", "john_doe", "password", roleService.getByName(RoleE.USER));
        user.setId(1L);

        registerRequest = new RegisterRequest("John", "Doe", "john.doe@example.com", "john_doe", "password");
        loginRequest = new LoginRequest("john_doe", "password");
    }

    @Test
    void testRegister() {
        when(userService.emailExists(registerRequest.getEmail())).thenReturn(false);
        when(userService.usernameExists(registerRequest.getUsername())).thenReturn(false);
        when(roleService.getByName(RoleE.USER)).thenReturn(user.getRole());
        when(userService.create(any(AppUser.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<?> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void testRegister_EmailExists() {
        when(userService.emailExists(registerRequest.getEmail())).thenReturn(true);

        assertThrows(CustomExceptionHandler.EmailExistsException.class, () -> authService.register(registerRequest));
    }

    @Test
    void testLogin_Success() {
        when(userService.getByUsername(loginRequest.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<?> response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void testLogin_FailedAuthentication() {
        when(userService.getByUsername(loginRequest.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(CustomExceptionHandler.PasswordNotCorrectException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testPasswordMail() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest("john_doe");
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(userService.getByUsername(passwordResetRequest.getUsername())).thenReturn(user);
        when(jwtService.generateResetToken(user)).thenReturn("reset-token");
        when(resetTokenService.create(any(ResetToken.class))).thenReturn(new ResetToken("reset-token", user, null));

        ResponseEntity<?> response = authService.passwordMail(passwordResetRequest, httpRequest);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void testPasswordReset() {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO("reset-token", "newPassword", "newPassword");

        when(jwtService.extractUsername("reset-token")).thenReturn("john_doe");
        when(userService.getByUsername("john_doe")).thenReturn(user);
        when(jwtService.isTokenValid("reset-token", user)).thenReturn(true);
        when(jwtService.extractClaim(eq("reset-token"), any())).thenReturn("PASSWORD_RESET");
        when(userService.update(user)).thenReturn(user);

        ResponseEntity<?> response = authService.passwordReset(passwordResetDTO);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }


    @Test
    void testPasswordReset_InvalidToken() {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO("invalid-token", "newPassword", "newPassword");

        when(jwtService.extractUsername("invalid-token")).thenThrow(RuntimeException.class);

        assertThrows(CustomExceptionHandler.InvalidTokenException.class, () -> authService.passwordReset(passwordResetDTO));
    }

    @Test
    void testPasswordReset_TokenMismatch() {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO("reset-token", "newPassword", "newPassword");

        when(jwtService.extractUsername("reset-token")).thenReturn("john_doe");
        when(userService.getByUsername("john_doe")).thenReturn(user);
        when(jwtService.isTokenValid("reset-token", user)).thenReturn(false);

        assertThrows(CustomExceptionHandler.InvalidTokenException.class, () -> authService.passwordReset(passwordResetDTO));
    }
}
