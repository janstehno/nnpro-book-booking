package service;

import cz.upce.nnpro.bookbooking.dto.BookDTO;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    @Mock private BookRepository bookRepository;

    @InjectMocks private BookService bookService;

    private Book book1, book2, book3;

    @BeforeEach
    void setUp() {
        book1 = new Book("Book A", "Author A", GenreE.FANTASY, "Desc A", false, 10, 8, 15.99);
        book2 = new Book("Book B", "Author B", GenreE.SCIENCE_FICTION, "Desc B", true, 0, 0, 9.99);
        book3 = new Book("Book C", "Author C", GenreE.FANTASY, "Desc C", false, 5, 1, 19.99);
        book1.setId(1L);
        book2.setId(2L);
        book3.setId(3L);
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
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3));

        List<ResponseBookDTO> bestBooks = bookService.getBest(1);

        assertEquals(1, bestBooks.size());
        assertEquals(book1.getTitle(), bestBooks.getFirst().getTitle());
        assertEquals(book1.getRating(), bestBooks.getFirst().getRating());
    }

    @Test
    void testGetBest_FromThreeBooks_LimitToTwo() {
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3));

        List<ResponseBookDTO> bestBooks = bookService.getBest(2);

        assertEquals(2, bestBooks.size());
        assertEquals(book1.getTitle(), bestBooks.getFirst().getTitle());
        assertEquals(book1.getRating(), bestBooks.getFirst().getRating());
        assertEquals(book2.getTitle(), bestBooks.getLast().getTitle());
        assertEquals(book2.getRating(), bestBooks.getLast().getRating());
    }

    @Test
    void testGetBest_FromThreeBooks_LimitToThree() {
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3));

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

    @Test
    void testCreateBook() {
        BookDTO bookDTO = new BookDTO(book1);
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        BookDTO result = bookService.createBook(bookDTO);

        assertNotNull(result);
        assertEquals(book1.getTitle(), result.getTitle());
        assertEquals(book1.getEbookPrice(), result.getEbookPrice());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        BookDTO updatedDTO = new BookDTO(book1);
        updatedDTO.setPhysicalCopies(15);

        when(bookRepository.findById(book1.getId())).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(book1);

        BookDTO result = bookService.updateBook(updatedDTO);

        assertNotNull(result);
        assertEquals(updatedDTO.getPhysicalCopies(), result.getPhysicalCopies());
        verify(bookRepository, times(1)).findById(book1.getId());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3));

        List<BookDTO> books = bookService.getAllBooks();

        assertEquals(3, books.size());
        assertEquals(book1.getTitle(), books.get(0).getTitle());
        assertEquals(book2.getTitle(), books.get(1).getTitle());
        assertEquals(book3.getTitle(), books.get(2).getTitle());
        verify(bookRepository, times(1)).findAll();
    }
}
