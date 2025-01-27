package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import lombok.Data;

@Data
public class ResponseGenreDTO {
    private GenreE genre;
    private String name;

    public ResponseGenreDTO(GenreE genre) {
        this.genre = genre;
        this.name = genre.getFormattedName();
    }
}
