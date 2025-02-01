package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.nnpro.bookbooking.controller.PurchaseController;
import cz.upce.nnpro.bookbooking.dto.RequestPurchaseDTO;
import cz.upce.nnpro.bookbooking.dto.ResponsePurchaseDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import cz.upce.nnpro.bookbooking.entity.join.BookPurchase;
import cz.upce.nnpro.bookbooking.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import utils.WithMockCustomUser;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PurchaseControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private PurchaseService purchaseService;

    @InjectMocks private PurchaseController purchaseController;

    private AppUser user;
    private Book book;
    private Purchase purchase;
    private BookPurchase bookPurchase;
    private ResponsePurchaseDTO purchaseDTO;
    private List<ResponsePurchaseDTO> purchaseDTOs;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(purchaseController).build();

        user = new AppUser();

        book = new Book("Book Title", "Test Author", GenreE.ACTION, "Test Description", true, 5, 3, 49.99);
        book.setId(1L);

        bookPurchase = new BookPurchase();
        bookPurchase.setBook(book);
        bookPurchase.setCount(2);

        purchase = new Purchase();
        purchase.setId(1L);
        purchase.setDate(LocalDate.now());
        purchase.setPrice(199.99);
        purchase.setBookPurchases(Collections.singleton(bookPurchase));
        purchase.setUser(user);

        purchaseDTO = new ResponsePurchaseDTO(purchase);
        purchaseDTOs = Collections.singletonList(purchaseDTO);
    }

    @Test
    @WithMockCustomUser
    void testGetAllPurchases() throws Exception {
        when(purchaseService.getAllByUserId(user.getId())).thenReturn(purchaseDTOs);

        mockMvc.perform(get("/purchases"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(purchase.getId()))
               .andExpect(jsonPath("$[0].date[0]").value(purchase.getDate().getYear()))
               .andExpect(jsonPath("$[0].date[1]").value(purchase.getDate().getMonthValue()))
               .andExpect(jsonPath("$[0].date[2]").value(purchase.getDate().getDayOfMonth()))
               .andExpect(jsonPath("$[0].price").value(purchase.getPrice()))
               .andExpect(jsonPath("$[0].books[0].id").value(book.getId()))
               .andExpect(jsonPath("$[0].books[0].title").value(book.getTitle()))
               .andExpect(jsonPath("$[0].books[0].count").value(bookPurchase.getCount()))
               .andExpect(jsonPath("$[0].books[0].price").value(book.getEbookPrice()));

        verify(purchaseService, times(1)).getAllByUserId(user.getId());
    }

    @Test
    @WithMockCustomUser
    void testGetPurchaseById() throws Exception {
        user.setId(1L);

        when(purchaseService.getByIdAndUserId(1L, user.getId())).thenReturn(purchaseDTO);

        mockMvc.perform(get("/purchases/{id}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(purchase.getId()))
               .andExpect(jsonPath("$.date[0]").value(purchase.getDate().getYear()))
               .andExpect(jsonPath("$.date[1]").value(purchase.getDate().getMonthValue()))
               .andExpect(jsonPath("$.date[2]").value(purchase.getDate().getDayOfMonth()))
               .andExpect(jsonPath("$.price").value(purchase.getPrice()))
               .andExpect(jsonPath("$.books[0].id").value(book.getId()))
               .andExpect(jsonPath("$.books[0].title").value(book.getTitle()))
               .andExpect(jsonPath("$.books[0].count").value(bookPurchase.getCount()))
               .andExpect(jsonPath("$.books[0].price").value(book.getEbookPrice()));

        verify(purchaseService, times(1)).getByIdAndUserId(1L, user.getId());
    }

    @Test
    @WithMockCustomUser
    void testCreatePurchase() throws Exception {
        List<RequestPurchaseDTO> requestPurchases = List.of(new RequestPurchaseDTO(1L, 2));

        when(purchaseService.create(user, requestPurchases)).thenReturn(purchaseDTO);

        mockMvc.perform(post("/purchases/new").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(requestPurchases)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(purchase.getId()))
               .andExpect(jsonPath("$.date[0]").value(purchase.getDate().getYear()))
               .andExpect(jsonPath("$.date[1]").value(purchase.getDate().getMonthValue()))
               .andExpect(jsonPath("$.date[2]").value(purchase.getDate().getDayOfMonth()))
               .andExpect(jsonPath("$.price").value(purchase.getPrice()))
               .andExpect(jsonPath("$.books[0].id").value(book.getId()))
               .andExpect(jsonPath("$.books[0].title").value(book.getTitle()))
               .andExpect(jsonPath("$.books[0].count").value(bookPurchase.getCount()))
               .andExpect(jsonPath("$.books[0].price").value(book.getEbookPrice()));

        verify(purchaseService, times(1)).create(user, requestPurchases);
    }
}

