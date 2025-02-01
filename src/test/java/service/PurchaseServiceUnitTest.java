package service;

import cz.upce.nnpro.bookbooking.dto.RequestPurchaseDTO;
import cz.upce.nnpro.bookbooking.dto.ResponsePurchaseDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.entity.join.BookPurchase;
import cz.upce.nnpro.bookbooking.repository.PurchaseRepository;
import cz.upce.nnpro.bookbooking.service.BookService;
import cz.upce.nnpro.bookbooking.service.MailService;
import cz.upce.nnpro.bookbooking.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PurchaseServiceUnitTest {

    @Mock private PurchaseRepository purchaseRepository;

    @Mock private BookService bookService;

    @Mock private MailService mailService;

    @InjectMocks private PurchaseService purchaseService;

    private AppUser user;
    private Book book1, book2;
    private Purchase purchase1, purchase2;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@user.com");

        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book A");
        book1.setEbook(true);
        book1.setEbookPrice(10.0);

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book B");
        book2.setEbook(false);

        purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setUser(user);
        purchase1.setBookPurchases(Set.of(new BookPurchase(book1, purchase1, 2)));
        purchase1.setPrice(20.0);

        purchase2 = new Purchase();
        purchase2.setId(1L);
        purchase2.setUser(user);
        purchase2.setBookPurchases(Collections.emptySet());
    }

    @Test
    void testGetAllPurchases() {
        when(purchaseRepository.findAll()).thenReturn(Collections.singletonList(purchase1));

        List<Purchase> result = purchaseService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void testGetPurchaseById() {
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase1));

        Purchase result = purchaseService.getById(1L);

        assertNotNull(result);
        assertEquals(purchase1, result);
    }

    @Test
    void testCreatePurchase() {
        Set<BookPurchase> bookPurchases = new HashSet<>();
        double totalPrice = 20.0;

        when(bookService.getById(book1.getId())).thenReturn(book1);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase1);

        List<RequestPurchaseDTO> purchaseData = Collections.singletonList(new RequestPurchaseDTO(book1.getId(), 2));

        ResponsePurchaseDTO result = purchaseService.create(user, purchaseData);

        assertNotNull(result);
        assertEquals(purchase1.getId(), result.getId());
    }

    @Test
    void testCreatePurchase_InvalidBook() {
        when(purchaseService.create(any(Purchase.class))).thenReturn(purchase2);
        when(bookService.getById(2L)).thenReturn(book2);

        List<RequestPurchaseDTO> purchaseData = Collections.singletonList(new RequestPurchaseDTO(2L, 1));

        ResponsePurchaseDTO result = purchaseService.create(user, purchaseData);

        assertNull(result);
    }

    @Test
    void testUpdatePurchase() {
        when(purchaseRepository.save(purchase1)).thenReturn(purchase1);

        Purchase result = purchaseService.update(purchase1);

        assertNotNull(result);
        assertEquals(purchase1, result);
    }

    @Test
    void testDeletePurchaseById() {
        doNothing().when(purchaseRepository).deleteById(1L);

        purchaseService.deleteById(1L);

        verify(purchaseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetAllPurchasesByUserId() {
        List<Purchase> purchases = Collections.singletonList(purchase1);
        when(purchaseRepository.findAllByUserId(1L)).thenReturn(purchases);

        List<ResponsePurchaseDTO> result = purchaseService.getAllByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetPurchaseByIdAndUserId() {
        when(purchaseRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(purchase1));

        ResponsePurchaseDTO result = purchaseService.getByIdAndUserId(1L, 1L);

        assertNotNull(result);
        assertEquals(purchase1.getId(), result.getId());
    }
}
