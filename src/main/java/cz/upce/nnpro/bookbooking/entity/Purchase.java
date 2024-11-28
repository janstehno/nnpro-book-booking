package cz.upce.nnpro.bookbooking.entity;

import cz.upce.nnpro.bookbooking.entity.join.BookPurchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private AppUser user;

    @Column
    @NotNull
    private LocalDate date;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.EAGER) private Set<BookPurchase> bookPurchases;

    @Column private double price;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return id != null && id.equals(purchase.id);
    }

    @Override
    public int hashCode() {
        return 31 + (id != null ? id.hashCode() : 0);
    }
}
