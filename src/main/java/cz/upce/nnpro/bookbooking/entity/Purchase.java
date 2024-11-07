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

    @Column
    @NotNull
    private double price;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @NotNull
    private Set<BookPurchase> bookPurchases;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }
}

