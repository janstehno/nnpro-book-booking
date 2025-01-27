package service;

import cz.upce.nnpro.bookbooking.Application;
import cz.upce.nnpro.bookbooking.dto.*;
import cz.upce.nnpro.bookbooking.entity.*;
import cz.upce.nnpro.bookbooking.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import utils.TestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class PurchaseServiceIntegrationTest {

    @Autowired private BookService bookService;

    @Autowired private PurchaseService purchaseService;

    @Autowired private AuthService authService;

    @Autowired private UserService userService;

    private final RegisterRequest testRegisterRequest = TestUtils.testRegisterRequest();

    private final Purchase testPurchase() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());
        Book b1 = bookService.create(TestUtils.testBook("Book 1"));
        RequestPurchaseDTO request = new RequestPurchaseDTO(List.of(b1.getId()));
        ResponsePurchaseDTO response = purchaseService.create(user, request);
        assertNotNull(response.getBooks());
        return purchaseService.getById(response.getId());
    }

    @Test
    void testGetAllByUserId() {
        Purchase purchase = testPurchase();
        Long userId = purchase.getUser().getId();

        List<ResponsePurchaseDTO> purchases = purchaseService.getAllByUserId(userId);
        Purchase tested = purchaseService.getById(purchases.getFirst().getId());

        assertFalse(purchases.isEmpty());
        assertEquals(userId, tested.getUser().getId());
    }

    @Test
    void testGetByIdAndUserId() {
        Purchase purchase = testPurchase();
        Long purchaseId = purchase.getId();
        Long userId = purchase.getUser().getId();

        ResponsePurchaseDTO response = purchaseService.getByIdAndUserId(purchaseId, userId);
        Purchase tested = purchaseService.getById(response.getId());

        assertNotNull(response);
        assertEquals(purchaseId, response.getId());
        assertEquals(userId, tested.getUser().getId());
    }

    @Test
    void testCreate() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());

        Book book1 = bookService.create(TestUtils.testBook("Book 1", 8.99));
        Book book2 = bookService.create(TestUtils.testBook("Book 2", 12.49));

        RequestPurchaseDTO request = new RequestPurchaseDTO(List.of(book1.getId(), book2.getId()));

        ResponsePurchaseDTO response = purchaseService.create(user, request);
        assertNotNull(response);
        assertNotNull(response.getBooks());
        assertEquals(2, response.getBooks().size());
        assertEquals(response.getPrice(), 8.99 + 12.49);
    }

    @Test
    void testCreate_InvalidBook() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());

        Book nonEbook = TestUtils.testBook("Non-Ebook Book");
        nonEbook.setEbook(false);
        Book nonEbookBook = bookService.create(nonEbook);
        RequestPurchaseDTO request = new RequestPurchaseDTO(List.of(nonEbookBook.getId()));

        ResponsePurchaseDTO response = purchaseService.create(user, request);
        assertNotNull(response);
        assertNotNull(response.getBooks());
        assertTrue(response.getBooks().isEmpty());
    }

    @Test
    void testCreate_NoBooks() {
        authService.register(testRegisterRequest);
        AppUser user = userService.getByUsername(testRegisterRequest.getUsername());

        RequestPurchaseDTO request = new RequestPurchaseDTO(List.of());

        ResponsePurchaseDTO response = purchaseService.create(user, request);
        assertNotNull(response);
        assertNotNull(response.getBooks());
        assertTrue(response.getBooks().isEmpty());
        assertEquals(response.getPrice(), 0.0);
    }
}
