package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ResponseBookingDTO {
    private Book book;
    private int count;
    private StatusE status;
    private LocalDate bookingDate;
    @Nullable private LocalDate expirationDate;
}
