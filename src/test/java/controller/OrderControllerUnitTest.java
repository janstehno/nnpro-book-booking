package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.nnpro.bookbooking.controller.OrderController;
import cz.upce.nnpro.bookbooking.dto.RequestOrderDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseOrderDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.OrderService;
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
import utils.WithMockCustomUser;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class OrderControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private OrderService orderService;

    @InjectMocks private OrderController orderController;

    private AppUser user;
    private Order order;
    private ResponseOrderDTO orderDTO;
    private List<ResponseOrderDTO> orderDTOs;
    private ResponseBookingDTO bookingDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        user = new AppUser();

        order = new Order();
        order.setId(1L);
        order.setDate(LocalDate.now());
        order.setBookings(Collections.emptyList());
        order.setUser(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBook(new Book());
        booking.setCount(2);
        booking.setStatus(StatusE.AVAILABLE);
        booking.setBookingDate(LocalDate.now());
        booking.setExpirationDate(LocalDate.now().plusDays(30));

        bookingDTO = new ResponseBookingDTO(booking);
        order.setBookings(Collections.singletonList(booking));

        orderDTO = new ResponseOrderDTO(order);
        orderDTOs = Collections.singletonList(orderDTO);
    }

    @Test
    @WithMockCustomUser
    void testGetAllOrders() throws Exception {
        when(orderService.getAllByUserId(user.getId())).thenReturn(orderDTOs);

        assertNotNull(bookingDTO.getExpirationDate());

        mockMvc.perform(get("/orders"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(order.getId()))
               .andExpect(jsonPath("$[0].date[0]").value(order.getDate().getYear()))
               .andExpect(jsonPath("$[0].date[1]").value(order.getDate().getMonthValue()))
               .andExpect(jsonPath("$[0].date[2]").value(order.getDate().getDayOfMonth()))
               .andExpect(jsonPath("$[0].bookings[0].id").value(bookingDTO.getId()))
               .andExpect(jsonPath("$[0].bookings[0].book").isNotEmpty())
               .andExpect(jsonPath("$[0].bookings[0].count").value(bookingDTO.getCount()))
               .andExpect(jsonPath("$[0].bookings[0].status").value(bookingDTO.getStatus().name()))
               .andExpect(jsonPath("$[0].bookings[0].bookingDate[0]").value(bookingDTO.getBookingDate().getYear()))
               .andExpect(jsonPath("$[0].bookings[0].bookingDate[1]").value(bookingDTO.getBookingDate().getMonthValue()))
               .andExpect(jsonPath("$[0].bookings[0].bookingDate[2]").value(bookingDTO.getBookingDate().getDayOfMonth()))
               .andExpect(jsonPath("$[0].bookings[0].expirationDate[0]").value(bookingDTO.getExpirationDate().getYear()))
               .andExpect(jsonPath("$[0].bookings[0].expirationDate[1]").value(bookingDTO.getExpirationDate().getMonthValue()))
               .andExpect(jsonPath("$[0].bookings[0].expirationDate[2]").value(bookingDTO.getExpirationDate().getDayOfMonth()));

        verify(orderService, times(1)).getAllByUserId(user.getId());
    }

    @Test
    @WithMockCustomUser
    void testGetOrderById() throws Exception {
        user.setId(1L);

        when(orderService.getByIdAndUserId(1L, user.getId())).thenReturn(orderDTO);

        assertNotNull(bookingDTO.getExpirationDate());

        mockMvc.perform(get("/orders/{id}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(order.getId()))
               .andExpect(jsonPath("$.date[0]").value(order.getDate().getYear()))
               .andExpect(jsonPath("$.date[1]").value(order.getDate().getMonthValue()))
               .andExpect(jsonPath("$.date[2]").value(order.getDate().getDayOfMonth()))
               .andExpect(jsonPath("$.bookings[0].id").value(bookingDTO.getId()))
               .andExpect(jsonPath("$.bookings[0].book").isNotEmpty())
               .andExpect(jsonPath("$.bookings[0].count").value(bookingDTO.getCount()))
               .andExpect(jsonPath("$.bookings[0].status").value(bookingDTO.getStatus().name()))
               .andExpect(jsonPath("$.bookings[0].bookingDate[0]").value(bookingDTO.getBookingDate().getYear()))
               .andExpect(jsonPath("$.bookings[0].bookingDate[1]").value(bookingDTO.getBookingDate().getMonthValue()))
               .andExpect(jsonPath("$.bookings[0].bookingDate[2]").value(bookingDTO.getBookingDate().getDayOfMonth()))
               .andExpect(jsonPath("$.bookings[0].expirationDate[0]").value(bookingDTO.getExpirationDate().getYear()))
               .andExpect(jsonPath("$.bookings[0].expirationDate[1]").value(bookingDTO.getExpirationDate().getMonthValue()))
               .andExpect(jsonPath("$.bookings[0].expirationDate[2]").value(bookingDTO.getExpirationDate().getDayOfMonth()));

        verify(orderService, times(1)).getByIdAndUserId(1L, user.getId());
    }

    @Test
    @WithMockCustomUser
    void testCreateOrder() throws Exception {
        List<RequestOrderDTO> requestOrders = List.of(new RequestOrderDTO(1L, 2, false));

        when(orderService.create(user, requestOrders)).thenReturn(orderDTO);

        assertNotNull(bookingDTO.getExpirationDate());

        mockMvc.perform(post("/orders/new").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(requestOrders)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(order.getId()))
               .andExpect(jsonPath("$.date[0]").value(order.getDate().getYear()))
               .andExpect(jsonPath("$.date[1]").value(order.getDate().getMonthValue()))
               .andExpect(jsonPath("$.date[2]").value(order.getDate().getDayOfMonth()))
               .andExpect(jsonPath("$.bookings[0].id").value(bookingDTO.getId()))
               .andExpect(jsonPath("$.bookings[0].book").isNotEmpty())
               .andExpect(jsonPath("$.bookings[0].count").value(bookingDTO.getCount()))
               .andExpect(jsonPath("$.bookings[0].status").value(bookingDTO.getStatus().name()))
               .andExpect(jsonPath("$.bookings[0].bookingDate[0]").value(bookingDTO.getBookingDate().getYear()))
               .andExpect(jsonPath("$.bookings[0].bookingDate[1]").value(bookingDTO.getBookingDate().getMonthValue()))
               .andExpect(jsonPath("$.bookings[0].bookingDate[2]").value(bookingDTO.getBookingDate().getDayOfMonth()))
               .andExpect(jsonPath("$.bookings[0].expirationDate[0]").value(bookingDTO.getExpirationDate().getYear()))
               .andExpect(jsonPath("$.bookings[0].expirationDate[1]").value(bookingDTO.getExpirationDate().getMonthValue()))
               .andExpect(jsonPath("$.bookings[0].expirationDate[2]").value(bookingDTO.getExpirationDate().getDayOfMonth()));

        verify(orderService, times(1)).create(user, requestOrders);
    }
}

