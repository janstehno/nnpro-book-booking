package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Order;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ResponseOrderDTO {
    private Long id;
    private LocalDate date;
    @Nullable private List<ResponseBookingDTO> bookings;

    public ResponseOrderDTO(Order order) {
        this.id = order.getId();
        this.date = order.getDate();
        this.bookings = order.getBookings().stream().map(ResponseBookingDTO::new).toList();
    }
}
