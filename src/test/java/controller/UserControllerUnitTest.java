package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.nnpro.bookbooking.controller.UserController;
import cz.upce.nnpro.bookbooking.dto.RequestUserPasswordDTO;
import cz.upce.nnpro.bookbooking.dto.UserDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.security.JwtService;
import cz.upce.nnpro.bookbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import utils.WithMockCustomUser;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private JwtService jwtService;

    @Mock private UserService userService;

    @InjectMocks private UserController userController;

    private AppUser user;
    private UserDTO userDTO;
    private RequestUserPasswordDTO requestUserPasswordDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new AppUser();

        userDTO = new UserDTO(user.getFirstname(), user.getLastname(), user.getEmail(), RoleE.USER);

        requestUserPasswordDTO = new RequestUserPasswordDTO("oldPassword", "newPassword");
    }

    @Test
    @WithMockCustomUser
    void testGetUser() throws Exception {
        when(userService.get(user)).thenReturn(userDTO);

        mockMvc.perform(get("/user"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstname").value(user.getFirstname()))
               .andExpect(jsonPath("$.lastname").value(user.getLastname()))
               .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
               .andExpect(jsonPath("$.role").value(userDTO.getRole().toString()));

        verify(userService, times(1)).get(user);
    }

    @Test
    @WithMockCustomUser
    void testUpdateUser() throws Exception {
        when(userService.updateProfile(any(AppUser.class), any(UserDTO.class))).thenReturn("generated-token");

        mockMvc.perform(put("/user").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(userDTO)))
               .andExpect(status().isOk())
               .andExpect(content().string("generated-token"));

        verify(userService, times(1)).updateProfile(user, userDTO);
    }

    @Test
    @WithMockCustomUser
    void testUpdateUserPassword() throws Exception {
        when(userService.updatePassword(any(AppUser.class), any(RequestUserPasswordDTO.class))).thenReturn("generated-token");

        mockMvc.perform(put("/user/password").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(requestUserPasswordDTO)))
               .andExpect(status().isOk())
               .andExpect(content().string("generated-token"));

        verify(userService, times(1)).updatePassword(user, requestUserPasswordDTO);
    }
}

