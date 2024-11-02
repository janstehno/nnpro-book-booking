package cz.upce.nnpro.bookbooking.entity;

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
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "BOOK_ID")
    @NotNull
    private Book book;

    @Column
    @NotNull
    private int rating;

    @Column private String text;

    @Column private LocalDate date;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        date = LocalDate.now();
    }
}

