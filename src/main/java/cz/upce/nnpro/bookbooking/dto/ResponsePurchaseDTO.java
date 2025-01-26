package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.entity.join.BookPurchase;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ResponsePurchaseDTO {
    private Long id;
    private LocalDate date;
    private double price;
    @Nullable private List<Book> books;

    public ResponsePurchaseDTO(Purchase purchase) {
        this.id = purchase.getId();
        this.date = purchase.getDate();
        this.price = purchase.getPrice();
        this.books = purchase.getBookPurchases().stream().map(BookPurchase::getBook).toList();
    }
}
