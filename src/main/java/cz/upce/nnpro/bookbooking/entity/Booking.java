package cz.upce.nnpro.bookbooking.entity;

import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
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

    @Column
    @NotNull
    private boolean online;

    @Enumerated(EnumType.STRING)
    @Column
    @NotNull
    private StatusE status;

    @Column
    @NotNull
    private LocalDate bookingDate;

    @Column private LocalDate expirationDate;

    @Column private LocalDate loanDate;

    @Column private LocalDate returnDate;

    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDate.now();
    }

    public Booking(Order order, Book book, int count, boolean online) {
        this.order = order;
        this.book = book;
        this.count = count;
        this.online = online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id != null && id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return 31 + (id != null ? id.hashCode() : 0);
    }
}
