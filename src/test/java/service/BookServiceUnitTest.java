package service;

import cz.upce.nnpro.bookbooking.dto.ResponseBookDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookDetailDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBooksDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import cz.upce.nnpro.bookbooking.repository.BookRepository;
import cz.upce.nnpro.bookbooking.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1, book2, book3;

    @BeforeEach
    void setUp() {
        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book A");
        book1.setRating(4.5);
        book1.setGenre(GenreE.FANTASY);
        book1.setEbookPrice(15.99);

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book B");
        book2.setRating(3.7);
        book2.setGenre(GenreE.SCIENCE_FICTION);
        book2.setEbookPrice(9.99);

        book3 = new Book();
        book3.setId(3L);
        book3.setTitle("Book C");
        book3.setRating(0.8);
        book3.setGenre(GenreE.FANTASY);
        book3.setEbookPrice(19.99);
    }

    @Test
    void testGetBookDetail() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        ResponseBookDetailDTO result = bookService.getBookDetail(1L);

        assertNotNull(result);
        assertEquals(book1, result.getBook());
    }

    @Test
    void testGetBest_FromThreeBooks_LimitToOne() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<ResponseBookDTO> bestBooks = bookService.getBest(1);

        assertEquals(1, bestBooks.size());
        assertEquals(book1.getTitle(), bestBooks.getFirst().getTitle());
        assertEquals(book1.getRating(), bestBooks.getFirst().getRating());
    }

    @Test
    void testGetBest_FromThreeBooks_LimitToTwo() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<ResponseBookDTO> bestBooks = bookService.getBest(2);

        assertEquals(2, bestBooks.size());
        assertEquals(book1.getTitle(), bestBooks.getFirst().getTitle());
        assertEquals(book1.getRating(), bestBooks.getFirst().getRating());
        assertEquals(book2.getTitle(), bestBooks.getLast().getTitle());
        assertEquals(book2.getRating(), bestBooks.getLast().getRating());
    }

    @Test
    void testGetBest_FromThreeBooks_LimitToThree() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<ResponseBookDTO> bestBooks = bookService.getBest(3);

        assertEquals(3, bestBooks.size());
        assertEquals(book1.getTitle(), bestBooks.getFirst().getTitle());
        assertEquals(book1.getRating(), bestBooks.getFirst().getRating());
        assertEquals(book3.getTitle(), bestBooks.getLast().getTitle());
        assertEquals(book3.getRating(), bestBooks.getLast().getRating());
    }

    @Test
    void testGetBest_FromOneBook_LimitToTwo() {
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book1));

        List<ResponseBookDTO> bestBooks = bookService.getBest(2);

        assertEquals(1, bestBooks.size());
        assertEquals(book1.getTitle(), bestBooks.getFirst().getTitle());
        assertEquals(book1.getRating(), bestBooks.getFirst().getRating());
    }

    @Test
    void testGetBest_FromZeroBooks() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<ResponseBookDTO> bestBooks = bookService.getBest(3);

        assertTrue(bestBooks.isEmpty());
    }

    @Test
    void testGetAllBooksFiltered_ShouldFilterByGenre() {
        List<String> genres = List.of("FANTASY");
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<Book> bookPage = new PageImpl<>(List.of(book1, book3), pageable, 2);

        when(bookRepository.findByGenreIn(Set.of(GenreE.FANTASY), pageable)).thenReturn(bookPage);

        ResponseBooksDTO result = bookService.getAllBooksFiltered(genres, "title", 1, 10);

        assertEquals(2, result.getTotal());
        assertEquals(book1.getTitle(), result.getBooks().getFirst().getTitle());
        assertEquals(book3.getTitle(), result.getBooks().getLast().getTitle());
    }

    @Test
    void testGetAllBooksFiltered_ShouldSortByPriceDesc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "ebookPrice"));
        Page<Book> bookPage = new PageImpl<>(List.of(book3, book1, book2), pageable, 3);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        ResponseBooksDTO result = bookService.getAllBooksFiltered(List.of(), "price-desc", 1, 10);

        assertEquals(3, result.getTotal());
        assertEquals(book3.getTitle(), result.getBooks().get(0).getTitle());
        assertEquals(book1.getTitle(), result.getBooks().get(1).getTitle());
        assertEquals(book2.getTitle(), result.getBooks().get(2).getTitle());
    }

    @Test
    void testGetAllBooksFiltered_ShouldReturnEmpty_WhenNoBooksMatch() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<Book> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(bookRepository.findByGenreIn(Set.of(GenreE.HISTORICAL), pageable)).thenReturn(emptyPage);

        ResponseBooksDTO result = bookService.getAllBooksFiltered(List.of("HISTORICAL"), "title", 1, 10);

        assertEquals(0, result.getTotal());
        assertTrue(result.getBooks().isEmpty());
    }

    @Test
    void testGetAllBooksFiltered_ShouldReturnAllSortedByTitle_WhenNonexistentGenreRequested() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<Book> bookPage = new PageImpl<>(List.of(book1, book2, book3), pageable, 3);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        ResponseBooksDTO result = bookService.getAllBooksFiltered(List.of("non-existent"), "title", 1, 10);

        assertEquals(3, result.getTotal());
        assertEquals(book1.getTitle(), result.getBooks().get(0).getTitle());
        assertEquals(book2.getTitle(), result.getBooks().get(1).getTitle());
        assertEquals(book3.getTitle(), result.getBooks().get(2).getTitle());
    }
}
