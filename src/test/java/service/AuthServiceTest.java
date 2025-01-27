package service;

import cz.upce.nnpro.bookbooking.dto.LoginRequest;
import cz.upce.nnpro.bookbooking.dto.PasswordResetDTO;
import cz.upce.nnpro.bookbooking.dto.PasswordResetRequest;
import cz.upce.nnpro.bookbooking.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.ResetToken;
import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.security.JwtService;
import cz.upce.nnpro.bookbooking.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import utils.TestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestComponent
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks private AuthService authService;

    @Mock private UserService userService;

    @Mock private RoleService roleService;

    @Mock private JwtService jwtService;

    @Mock private MailService mailService;

    @Mock private ResetTokenService resetTokenService;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private AuthenticationManager authenticationManager;

    @Test
    void register_shouldCreateUser_whenValidRequest() {
        RegisterRequest request = TestUtils.testRegisterRequest;

        when(userService.emailExists(request.getEmail())).thenReturn(false);
        when(userService.usernameExists(request.getUsername())).thenReturn(false);
        when(roleService.getByName(any(RoleE.class))).thenReturn(new Role(RoleE.USER));
        when(userService.create(any(AppUser.class))).thenReturn(TestUtils.testAppUser);

        ResponseEntity<?> response = authService.register(request);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void login_shouldAuthenticateUser_whenValidCredentials() {
        AppUser user = TestUtils.testAppUser;
        LoginRequest request = new LoginRequest(user.getUsername(), user.getPassword());

        when(userService.getByUsername(request.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));

        ResponseEntity<?> response = authService.login(request);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void login_shouldThrowException_whenInvalidPassword() {
        AppUser user = TestUtils.testAppUser;
        LoginRequest request = new LoginRequest(user.getUsername(), "wrongPassword");

        when(userService.getByUsername(request.getUsername())).thenReturn(user);

        assertThrows(CustomExceptionHandler.PasswordNotCorrectException.class, () -> authService.login(request));
    }

    @Test
    void passwordMail_shouldSendEmail_whenValidUsername() {
        AppUser user = TestUtils.testAppUser;
        PasswordResetRequest request = new PasswordResetRequest(user.getUsername());

        when(userService.getByUsername(request.getUsername())).thenReturn(user);

        when(jwtService.generateResetToken(user)).thenReturn("mockedToken");
        ResetToken mockToken = new ResetToken("mockedToken", user, LocalDateTime.now().plusMinutes(15));
        when(resetTokenService.create(any(ResetToken.class))).thenReturn(mockToken);

        doNothing().when(mailService).sendEmailAboutPasswordReset(anyString(), anyString());

        ResponseEntity<?> response = authService.passwordMail(request, mock(HttpServletRequest.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void passwordReset_shouldUpdatePassword_whenValidRequest() {
        AppUser user = TestUtils.testAppUser;
        PasswordResetDTO request = new PasswordResetDTO("token", "newPassword", "newPassword");

        when(jwtService.extractUsername(request.getToken())).thenReturn(user.getUsername());
        when(userService.getByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid(request.getToken(), user)).thenReturn(true);
        when(jwtService.extractClaim(eq(request.getToken()), any())).thenReturn("PASSWORD_RESET");
        when(resetTokenService.getByToken(request.getToken())).thenReturn(null);

        ResponseEntity<?> response = authService.passwordReset(request);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void passwordReset_shouldThrowException_whenPasswordsDoNotMatch() {
        PasswordResetDTO request = new PasswordResetDTO("token", "newPassword", "differentPassword");

        assertThrows(CustomExceptionHandler.PasswordNotCorrectException.class, () -> authService.passwordReset(request));
    }
}
