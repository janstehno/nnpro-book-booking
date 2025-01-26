package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseBookingDTO {
    private Long id;
    private Book book;
    private int count;
    private StatusE status;
    private LocalDate bookingDate;
    @Nullable private LocalDate expirationDate;

    public ResponseBookingDTO(Booking booking) {
        this.id = booking.getId();
        this.book = booking.getBook();
        this.count = booking.getCount();
        this.status = booking.getStatus();
        this.bookingDate = booking.getBookingDate();
        this.expirationDate = booking.getExpirationDate();
    }
}
