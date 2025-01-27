package service;

import cz.upce.nnpro.bookbooking.Application;
import cz.upce.nnpro.bookbooking.dto.LoginRequest;
import cz.upce.nnpro.bookbooking.dto.PasswordResetDTO;
import cz.upce.nnpro.bookbooking.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.ResetToken;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.security.JwtService;
import cz.upce.nnpro.bookbooking.service.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import utils.TestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class AuthServiceIntegrationTest {

    @Autowired private AuthService authService;

    @Autowired private UserService userService;

    @Autowired private RoleService roleService;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private JwtService jwtService;

    @Autowired private ResetTokenService resetTokenService;

    private final RegisterRequest testRegisterRequest = TestUtils.testRegisterRequest();

    @Test
    void register_shouldCreateUser_whenValidRequest() {
        ResponseEntity<?> response = authService.register(testRegisterRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AppUser createdUser = userService.getByUsername(testRegisterRequest.getUsername());
        assertNotNull(createdUser);
        assertEquals(testRegisterRequest.getUsername(), createdUser.getUsername());
    }

    @Test
    void register_shouldThrowUsernameExistsException_whenUsernameAlreadyExists() {
        authService.register(testRegisterRequest);
        RegisterRequest request = new RegisterRequest(testRegisterRequest.getFirstname(),
                                                      testRegisterRequest.getLastname(),
                                                      "unique@example.com",
                                                      testRegisterRequest.getUsername(),
                                                      testRegisterRequest.getPassword());
        assertThrows(CustomExceptionHandler.UsernameExistsException.class, () -> authService.register(request));
    }

    @Test
    void register_shouldThrowEmailExistsException_whenEmailAlreadyExists() {
        authService.register(testRegisterRequest);
        RegisterRequest request = new RegisterRequest(testRegisterRequest.getFirstname(),
                                                      testRegisterRequest.getLastname(),
                                                      "test@example.com",
                                                      "newUsername",
                                                      testRegisterRequest.getPassword());
        assertThrows(CustomExceptionHandler.EmailExistsException.class, () -> authService.register(request));
    }

    @Test
    void login_shouldAuthenticateUser_whenValidCredentials() {
        authService.register(testRegisterRequest);
        LoginRequest request = new LoginRequest(testRegisterRequest.getUsername(), testRegisterRequest.getPassword());
        assertEquals(HttpStatus.OK, authService.login(request).getStatusCode());
    }

    @Test
    void login_shouldThrowPasswordNotCorrectException_whenInvalidPassword() {
        authService.register(testRegisterRequest);
        LoginRequest request = new LoginRequest(testRegisterRequest.getUsername(), "invalidPassword");
        assertThrows(CustomExceptionHandler.PasswordNotCorrectException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrowPasswordNotCorrectException_whenInvalidUsername() {
        authService.register(testRegisterRequest);
        LoginRequest request = new LoginRequest("nonexistentuser", testRegisterRequest.getPassword());
        assertThrows(CustomExceptionHandler.UsernameNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void passwordReset_shouldUpdatePassword_whenValidRequest() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());

        String token = jwtService.generateResetToken(user);
        ResetToken resetToken = new ResetToken(token, user, LocalDateTime.now().plusMinutes(15));
        resetTokenService.create(resetToken);

        PasswordResetDTO passwordResetDTO = new PasswordResetDTO(token, "newPassword123", "newPassword123");
        ResponseEntity<?> response = authService.passwordReset(passwordResetDTO);

        AppUser updatedUser = userService.getById(user.getId());
        assertTrue(passwordEncoder.matches("newPassword123", updatedUser.getPassword()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void passwordReset_shouldThrowInvalidTokenException_whenTokenIsInvalid() {
        String invalidToken = "invalid-token";
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO(invalidToken, "newPassword123", "newPassword123");
        assertThrows(CustomExceptionHandler.InvalidTokenException.class, () -> authService.passwordReset(passwordResetDTO));
    }

    @Test
    void passwordReset_shouldThrowPasswordNotCorrectException_whenPasswordsDoNotMatch() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());

        String token = jwtService.generateResetToken(user);
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO(token, "newPassword123", "differentPassword");
        assertThrows(CustomExceptionHandler.PasswordNotCorrectException.class, () -> authService.passwordReset(passwordResetDTO));
    }

}
