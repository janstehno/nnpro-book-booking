package cz.upce.nnpro.bookbooking.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseBooksDTO {
    private List<ResponseBookDTO> books;
    private long total;

    public ResponseBooksDTO(List<ResponseBookDTO> books, long total) {
        this.books = books;
        this.total = total;
    }
}
