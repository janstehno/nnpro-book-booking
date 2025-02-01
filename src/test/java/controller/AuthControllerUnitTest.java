package controller;

import cz.upce.nnpro.bookbooking.controller.AuthController;
import cz.upce.nnpro.bookbooking.dto.LoginRequest;
import cz.upce.nnpro.bookbooking.dto.PasswordResetDTO;
import cz.upce.nnpro.bookbooking.dto.PasswordResetRequest;
import cz.upce.nnpro.bookbooking.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private AuthService authService;

    @InjectMocks private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("test", "user", "user@example.com", "user", "password");

        when(authService.register(registerRequest)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                                              .content(
                                                      "{ \"username\": \"user\", \"firstname\": \"test\", \"lastname\": \"user\", \"email\": \"user@example.com\", \"password\": \"password\" }"))
               .andExpect(status().isOk());

        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "password");

        when(authService.login(loginRequest)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{ \"username\": \"user\", \"password\": \"password\" }"))
               .andExpect(status().isOk());

        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void testPasswordMail() throws Exception {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest("user");

        when(authService.passwordMail(eq(passwordResetRequest), any(HttpServletRequest.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/auth/password").contentType(MediaType.APPLICATION_JSON).content("{ \"username\": \"user\" }")).andExpect(status().isOk());

        verify(authService, times(1)).passwordMail(eq(passwordResetRequest), any(HttpServletRequest.class));
    }

    @Test
    void testPasswordReset() throws Exception {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO("token", "newPassword", "newPassword");

        when(authService.passwordReset(passwordResetDTO)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/auth/password/reset").contentType(MediaType.APPLICATION_JSON)
                                                    .content("{ \"token\": \"token\", \"newPassword\": \"newPassword\", \"confirmPassword\": \"newPassword\" }"))
               .andExpect(status().isOk());

        verify(authService, times(1)).passwordReset(passwordResetDTO);
    }
}
