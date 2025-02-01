package controller;

import cz.upce.nnpro.bookbooking.controller.AdminController;
import cz.upce.nnpro.bookbooking.dto.RequestBookingsDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseUserDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.AdminService;
import cz.upce.nnpro.bookbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AdminControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private AdminService adminService;

    @Mock private UserService userService;

    @InjectMocks private AdminController adminController;

    private ResponseBookingDTO bookingDTO;
    private AppUser user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(StatusE.AVAILABLE);
        booking.setBookingDate(LocalDate.now());
        booking.setCount(1);
        booking.setExpirationDate(LocalDate.now().plusDays(7));

        bookingDTO = new ResponseBookingDTO(booking);

        Role role = new Role();
        role.setName(RoleE.USER);

        user = new AppUser("Test", "User", "test@user.com", "user", "user", role);
        user.setId(1L);
    }

    @Test
    void testGetAllBookingsOfUser() throws Exception {
        List<ResponseBookingDTO> bookings = Collections.singletonList(bookingDTO);
        when(adminService.getAllByUserId(1L)).thenReturn(bookings);

        mockMvc.perform(get("/admin/users/{userId}/bookings", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(booking.getId()))
               .andExpect(jsonPath("$[0].status").value(booking.getStatus().name()));

        verify(adminService, times(1)).getAllByUserId(1L);
    }

    @Test
    void testUpdateBookings() throws Exception {
        RequestBookingsDTO request = new RequestBookingsDTO();
        request.setReturnIds(List.of(1L));
        request.setLoanIds(List.of(2L));

        List<ResponseBookingDTO> bookings = Collections.singletonList(bookingDTO);
        when(adminService.getAllByUserId(1L)).thenReturn(bookings);

        mockMvc.perform(put("/admin/users/{userId}/bookings", 1L).contentType(MediaType.APPLICATION_JSON).content("{ \"returnIds\": [1], \"loanIds\": [2] }"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(booking.getId()))
               .andExpect(jsonPath("$[0].status").value(booking.getStatus().name()));

        verify(adminService, times(1)).updateReturnedBooks(1L, List.of(1L));
        verify(adminService, times(1)).updateLoanedBooks(1L, List.of(2L));
    }

    @Test
    void testGetAllUsers() throws Exception {
        ResponseUserDTO userDTO = new ResponseUserDTO(user);
        List<ResponseUserDTO> users = Collections.singletonList(userDTO);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(user.getId()))
               .andExpect(jsonPath("$[0].username").value(user.getUsername()))
               .andExpect(jsonPath("$[0].firstname").value(user.getFirstname()))
               .andExpect(jsonPath("$[0].lastname").value(user.getLastname()))
               .andExpect(jsonPath("$[0].email").value(user.getEmail()))
               .andExpect(jsonPath("$[0].role").value(user.getRole().getName().name()));

        verify(userService, times(1)).getAllUsers();
    }
}
