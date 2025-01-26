package cz.upce.nnpro.bookbooking.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.HashMap;

@Getter
@AllArgsConstructor
public enum GenreE {
    THRILLER("Thriller"),
    COMEDY("Comedy"),
    ROMANCE("Romance"),
    ACTION("Action"),
    DRAMA("Drama"),
    SCIENCE_FICTION("Science Fiction"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    MYSTERY("Mystery"),
    BIOGRAPHY("Biography"),
    HISTORICAL("Historical"),
    NON_FICTION("Non-fiction"),
    YOUNG_ADULT("Young Adult"),
    CLASSIC("Classic"),
    DYSTOPIAN("Dystopian");

    private final String formattedName;

    private static final Map<GenreE, String> genreMap = new HashMap<>();

    static {
        for (GenreE genre : GenreE.values()) {
            genreMap.put(genre, genre.formattedName);
        }
    }
}