package cz.upce.nnpro.bookbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String title;

    @Column private String author;

    @Enumerated(EnumType.STRING)
    @Column
    private GenreE genre;

    @Column private String description;

    @Column
    @NotNull
    private boolean isPhysical;

    @Column
    @NotNull
    private boolean isEbook;

    @Column private int physicalCopies;

    @Column private int availableCopies;

    @Column
    @NotNull
    private double ebookPrice;

    @Transient private Double rating = 0.0;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Review> reviews;

    public Book(String title, String author, GenreE genre, String description, boolean isPhysical, boolean isEbook, int physicalCopies, int availableCopies,
                double ebookPrice) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.isPhysical = isPhysical;
        this.isEbook = isEbook;
        this.physicalCopies = physicalCopies;
        this.availableCopies = availableCopies;
        this.ebookPrice = ebookPrice;
    }

    public Double getRating() {
        int rating = 0;
        assert reviews != null;
        for (Review review : reviews) {
            rating += review.getRating();
        }
        return rating == 0 ? 0.0 : (double) rating / reviews.size();
    }
}

