package cz.upce.nnpro.bookbooking.entity;

import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}

