package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.Purchase;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ResponsePurchaseDTO {
    private Long id;
    private LocalDate date;
    private double price;
    private List<ResponsePurchaseBookDTO> books;

    public ResponsePurchaseDTO(Purchase purchase) {
        this.id = purchase.getId();
        this.date = purchase.getDate();
        this.price = purchase.getPrice();
        this.books = purchase.getBookPurchases()
                             .stream()
                             .map(bp -> new ResponsePurchaseBookDTO(bp.getBook().getId(), bp.getBook().getTitle(), bp.getCount(), bp.getBook().getEbookPrice()))
                             .toList();
    }
}
