package service;

import cz.upce.nnpro.bookbooking.dto.RequestOrderDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseOrderDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.repository.OrderRepository;
import cz.upce.nnpro.bookbooking.security.RedisService;
import cz.upce.nnpro.bookbooking.service.BookingService;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.MailService;
import cz.upce.nnpro.bookbooking.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    @Mock private OrderRepository orderRepository;

    @Mock private BookService bookService;

    @Mock private BookingService bookingService;

    @Mock private MailService mailService;

    @Mock private RedisService redisService;

    @InjectMocks private OrderService orderService;

    private AppUser user;
    private Book book1, book2;
    private Order order1;
    private Booking booking1;

    private void mockRedisService() {
        RLock mockLock = mock(RLock.class);
        when(redisService.getLock(anyString())).thenReturn(mockLock);
        when(redisService.tryLock(any(RLock.class), anyLong(), any(TimeUnit.class))).thenReturn(true);
        doNothing().when(redisService).releaseLock(any(RLock.class));
    }

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@user.com");

        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book A");
        book1.setPhysical();
        book1.setPhysicalCopies(5);
        book1.setAvailableCopies(5);

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book B");
        book1.setPhysical();
        book2.setPhysicalCopies(0);
        book2.setAvailableCopies(0);

        booking1 = new Booking();
        booking1.setBook(book1);
        booking1.setCount(2);

        order1 = new Order();
        order1.setId(1L);
        order1.setUser(user);
        order1.setBookings(Collections.emptyList());
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order1));

        List<Order> result = orderService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        Order result = orderService.getById(1L);

        assertNotNull(result);
        assertEquals(order1, result);
    }

    @Test
    void testCreateOrder() {
        List<Booking> bookings = Collections.singletonList(booking1);

        when(bookService.getById(book1.getId())).thenReturn(book1);
        when(bookingService.createAll(anySet())).thenReturn(bookings);
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        mockRedisService();

        ResponseOrderDTO result = orderService.create(user, Collections.singletonList(new RequestOrderDTO(book1.getId(), 2, false)));

        assertNotNull(result);
        assertEquals(order1.getId(), result.getId());
    }

    @Test
    void testUpdateOrder() {
        when(orderRepository.save(order1)).thenReturn(order1);

        Order result = orderService.update(order1);

        assertNotNull(result);
        assertEquals(order1, result);
    }

    @Test
    void testDeleteOrderById() {
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteById(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetAllOrdersByUserId() {
        List<Order> orders = Collections.singletonList(order1);
        when(orderRepository.findAllByUserId(1L)).thenReturn(orders);

        List<ResponseOrderDTO> result = orderService.getAllByUserId(1L);

        assertEquals(1, result.size());
    }


    @Test
    void testGetOrderByIdAndUserId() {
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order1));

        ResponseOrderDTO result = orderService.getByIdAndUserId(1L, 1L);

        assertNotNull(result);
        assertEquals(order1.getId(), result.getId());
    }

    @Test
    void testCreateOrder_InvalidBook() {
        RequestOrderDTO invalidRequest = new RequestOrderDTO(2L, 1, false);
        List<RequestOrderDTO> requestData = Collections.singletonList(invalidRequest);

        when(bookService.getById(2L)).thenReturn(book2);
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        mockRedisService();

        ResponseOrderDTO result = orderService.create(user, requestData);

        assertNull(result);
    }

    @Test
    void testCreateOrder_Successful() {
        RequestOrderDTO validRequest = new RequestOrderDTO(1L, 2, false);
        List<RequestOrderDTO> requestData = Collections.singletonList(validRequest);

        when(bookService.getById(1L)).thenReturn(book1);
        when(bookingService.createAll(anySet())).thenReturn(Collections.singletonList(booking1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        mockRedisService();

        ResponseOrderDTO result = orderService.create(user, requestData);

        assertNotNull(result.getBookings());
        assertFalse(result.getBookings().isEmpty());
        assertEquals(1L, result.getId());
    }
}

