package cz.upce.nnpro.bookbooking.entity;

import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @NotNull
    private Order order;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @NotNull
    private Book book;

    @Column
    @NotNull
    private int count;

    @Enumerated(EnumType.STRING)
    @Column
    @NotNull
    private StatusE status;

    @Column
    @NotNull
    private LocalDate bookingDate;

    @Column
    @NotNull
    private LocalDate expirationDate;

    @Column private LocalDate loanDate;

    @Column private LocalDate returnDate;

    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDate.now();
    }
}

