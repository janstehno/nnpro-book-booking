package controller;

import cz.upce.nnpro.bookbooking.controller.BookController;
import cz.upce.nnpro.bookbooking.dto.ResponseBookDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBooksDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import cz.upce.nnpro.bookbooking.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BookControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private BookService bookService;

    @InjectMocks private BookController bookController;

    private Book book;
    private ResponseBookDTO bookDTO;
    private ResponseBookDetailDTO bookDetailDTO;
    private ResponseBooksDTO booksDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        bookDTO = new ResponseBookDTO(book);
        bookDetailDTO = new ResponseBookDetailDTO(book);

        booksDTO = new ResponseBooksDTO(Collections.singletonList(bookDTO), 1);
    }

    @Test
    void testGetAllBooks() throws Exception {
        List<Book> books = Collections.singletonList(book);
        when(bookService.getAll()).thenReturn(books);

        mockMvc.perform(get("/books"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(book.getId()))
               .andExpect(jsonPath("$[0].title").value(book.getTitle()));

        verify(bookService, times(1)).getAll();
    }

    @Test
    void testGetAllBooksFiltered() throws Exception {
        List<String> genres = List.of("FICTION");
        when(bookService.getAllBooksFiltered(genres, "name", 1, 10)).thenReturn(booksDTO);

        mockMvc.perform(get("/books/filtered")
                                .param("genres", "FICTION")
                                .param("sort", "name")
                                .param("page", "1")
                                .param("size", "10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.books[0].id").value(book.getId()))
               .andExpect(jsonPath("$.books[0].title").value(book.getTitle()));

        verify(bookService, times(1)).getAllBooksFiltered(genres, "name", 1, 10);
    }

    @Test
    void testGetBestBooks() throws Exception {
        List<ResponseBookDTO> bestBooks = Collections.singletonList(bookDTO);
        when(bookService.getBest(10)).thenReturn(bestBooks);

        mockMvc.perform(get("/books/best").param("limit", "10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(book.getId()))
               .andExpect(jsonPath("$[0].title").value(book.getTitle()));

        verify(bookService, times(1)).getBest(10);
    }

    @Test
    void testGetBookGenres() throws Exception {
        GenreE[] genres = GenreE.values();

        mockMvc.perform(get("/books/genres"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value(genres[0].getFormattedName()));
    }

    @Test
    void testGetBookById() throws Exception {
        when(bookService.getBookDetail(1L)).thenReturn(bookDetailDTO);

        mockMvc.perform(get("/books/{id}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.book.id").value(book.getId()))
               .andExpect(jsonPath("$.book.title").value(book.getTitle()))
               .andExpect(jsonPath("$.book.author").value(book.getAuthor()));

        verify(bookService, times(1)).getBookDetail(1L);
    }
}
