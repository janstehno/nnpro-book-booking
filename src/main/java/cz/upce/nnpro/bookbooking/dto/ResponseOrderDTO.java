package cz.upce.nnpro.bookbooking.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ResponseOrderDTO {
    private Long id;
    private LocalDate date;
    @Nullable private List<ResponseBookingDTO> bookings;
}
