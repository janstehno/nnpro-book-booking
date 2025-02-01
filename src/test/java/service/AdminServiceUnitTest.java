package service;

import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.service.AdminService;
import cz.upce.nnpro.bookbooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AdminServiceUnitTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private AdminService adminService;

    private Booking booking1, booking2;

    @BeforeEach
    void setUp() {
        Order order1 = new Order();
        order1.setId(1L);

        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(StatusE.AVAILABLE);
        booking1.setOrder(order1);

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(StatusE.LOANED);
        booking2.setOrder(order1);
    }

    @Test
    void testGetAllByUserId() {
        when(bookingService.getAllByOrderUserId(1L)).thenReturn(List.of(booking1, booking2));

        List<ResponseBookingDTO> result = adminService.getAllByUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking1.getId(), result.get(0).getId());
        assertEquals(booking2.getId(), result.get(1).getId());
    }

    @Test
    void testUpdateReturnedBooks() {
        List<Long> bookingIds = List.of(1L, 2L);
        when(bookingService.getAllByOrderUserIdAndIdIn(1L, bookingIds)).thenReturn(List.of(booking1, booking2));

        adminService.updateReturnedBooks(1L, bookingIds);

        verify(bookingService, times(0)).updateReturned(booking1);
        verify(bookingService, times(1)).updateReturned(booking2);
    }

    @Test
    void testUpdateLoanedBooks() {
        List<Long> bookingIds = List.of(1L, 2L);
        when(bookingService.getAllByOrderUserIdAndIdIn(1L, bookingIds)).thenReturn(List.of(booking1, booking2));

        adminService.updateLoanedBooks(1L, bookingIds);

        verify(bookingService, times(1)).updateLoaned(booking1);
        verify(bookingService, times(0)).updateLoaned(booking2);
    }

    @Test
    void testUpdateReturnedBooks_EmptyBookingIds() {
        adminService.updateReturnedBooks(1L, Collections.emptyList());

        verify(bookingService, times(0)).updateReturned(any());
    }

    @Test
    void testUpdateLoanedBooks_EmptyBookingIds() {
        adminService.updateLoanedBooks(1L, Collections.emptyList());

        verify(bookingService, times(0)).updateLoaned(any());
    }

    @Test
    void testUpdateReturnedBooks_NoBookings() {
        when(bookingService.getAllByOrderUserIdAndIdIn(1L, List.of(1L, 2L)))
                .thenReturn(Collections.emptyList());

        adminService.updateReturnedBooks(1L, List.of(1L, 2L));

        verify(bookingService, times(0)).updateReturned(any());
    }

    @Test
    void testUpdateLoanedBooks_NoBookings() {
        when(bookingService.getAllByOrderUserIdAndIdIn(1L, List.of(1L, 2L)))
                .thenReturn(Collections.emptyList());

        adminService.updateLoanedBooks(1L, List.of(1L, 2L));

        verify(bookingService, times(0)).updateLoaned(any());
    }
}
