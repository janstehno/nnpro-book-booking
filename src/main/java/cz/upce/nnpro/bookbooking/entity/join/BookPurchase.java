package cz.upce.nnpro.bookbooking.entity.join;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_purchase", uniqueConstraints = {@UniqueConstraint(columnNames = {"purchase_id", "book_id"})})
public class BookPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    @NotNull
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @NotNull
    private Book book;

    @Column
    @NotNull
    private Integer count;

    public BookPurchase(Book book, Purchase purchase, Integer count) {
        this.book = book;
        this.purchase = purchase;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookPurchase that = (BookPurchase) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31 + (id != null ? id.hashCode() : 0);
    }
}

