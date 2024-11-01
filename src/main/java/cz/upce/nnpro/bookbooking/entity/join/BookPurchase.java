package cz.upce.nnpro.bookbooking.entity.join;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_purchase", uniqueConstraints = {@UniqueConstraint(columnNames = {"purchase_id", "book_id"})})
public class BookPurchase {

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    @NotNull
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @NotNull
    private Book book;
}

