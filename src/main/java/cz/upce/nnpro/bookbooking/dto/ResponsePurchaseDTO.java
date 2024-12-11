package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Book;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ResponsePurchaseDTO {
    private Long id;
    private LocalDate date;
    private double price;
    @Nullable private List<Book> books;
}
