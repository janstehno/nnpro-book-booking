package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.nnpro.bookbooking.controller.AdminController;
import cz.upce.nnpro.bookbooking.dto.BookDTO;
import cz.upce.nnpro.bookbooking.dto.RequestBookingsDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseUserDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.AdminService;
import cz.upce.nnpro.bookbooking.service.BookService;
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

    @Mock private BookService bookService;

    @InjectMocks private AdminController adminController;

    private ResponseBookingDTO bookingDTO;
    private AppUser user;
    private Booking booking;
    private BookDTO bookDTO;

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

        bookDTO = new BookDTO(1L, "Test Book", "Test Author", "ACTION",
                              "Description", false, 5, 29.99);
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

        mockMvc.perform(put("/admin/users/{userId}/bookings", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"returnIds\": [1], \"loanIds\": [2] }"))
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

    @Test
    void testCreateBook() throws Exception {
        when(bookService.createBook(any(BookDTO.class))).thenReturn(bookDTO);

        mockMvc.perform(post("/admin/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(bookDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(bookDTO.getId()))
               .andExpect(jsonPath("$.title").value(bookDTO.getTitle()))
               .andExpect(jsonPath("$.author").value(bookDTO.getAuthor()))
               .andExpect(jsonPath("$.genre").value(bookDTO.getGenre().name()))
               .andExpect(jsonPath("$.description").value(bookDTO.getDescription()))
               .andExpect(jsonPath("$.online").value(bookDTO.isOnline()))
               .andExpect(jsonPath("$.physicalCopies").value(bookDTO.getPhysicalCopies()))
               .andExpect(jsonPath("$.ebookPrice").value(bookDTO.getEbookPrice()));

        verify(bookService, times(1)).createBook(any(BookDTO.class));
    }

    @Test
    void testUpdateBook() throws Exception {
        when(bookService.updateBook(any(BookDTO.class))).thenReturn(bookDTO);

        mockMvc.perform(put("/admin/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(bookDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(bookDTO.getId()))
               .andExpect(jsonPath("$.title").value(bookDTO.getTitle()))
               .andExpect(jsonPath("$.author").value(bookDTO.getAuthor()))
               .andExpect(jsonPath("$.genre").value(bookDTO.getGenre().name()))
               .andExpect(jsonPath("$.description").value(bookDTO.getDescription()))
               .andExpect(jsonPath("$.online").value(bookDTO.isOnline()))
               .andExpect(jsonPath("$.physicalCopies").value(bookDTO.getPhysicalCopies()))
               .andExpect(jsonPath("$.ebookPrice").value(bookDTO.getEbookPrice()));

        verify(bookService, times(1)).updateBook(any(BookDTO.class));
    }

    @Test
    void testGetAllBooks() throws Exception {
        List<BookDTO> books = Collections.singletonList(bookDTO);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/admin/books"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(bookDTO.getId()))
               .andExpect(jsonPath("$[0].title").value(bookDTO.getTitle()))
               .andExpect(jsonPath("$[0].author").value(bookDTO.getAuthor()))
               .andExpect(jsonPath("$[0].genre").value(bookDTO.getGenre().name()))
               .andExpect(jsonPath("$[0].description").value(bookDTO.getDescription()))
               .andExpect(jsonPath("$[0].online").value(bookDTO.isOnline()))
               .andExpect(jsonPath("$[0].physicalCopies").value(bookDTO.getPhysicalCopies()))
               .andExpect(jsonPath("$[0].ebookPrice").value(bookDTO.getEbookPrice()));

        verify(bookService, times(1)).getAllBooks();
    }
}
