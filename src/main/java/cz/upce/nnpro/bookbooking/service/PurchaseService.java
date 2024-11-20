package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.RequestPurchaseDTO;
import cz.upce.nnpro.bookbooking.dto.ResponsePurchaseDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.entity.join.BookPurchase;
import cz.upce.nnpro.bookbooking.repository.PurchaseRepository;
import cz.upce.nnpro.bookbooking.security.service.MailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class PurchaseService implements ServiceInterface<Purchase> {

    private final PurchaseRepository purchaseRepository;

    private final BookService bookService;

    private final MailService mailService;

    @Override
    public List<Purchase> getAll() {
        return purchaseRepository.findAll();
    }

    @Override
    public Purchase getById(Long id) throws RuntimeException {
        return purchaseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Purchase create(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public Purchase update(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public void deleteById(Long id) {
        purchaseRepository.deleteById(id);
    }

    public List<ResponsePurchaseDTO> getAllByUserId(Long userId) {
        return purchaseRepository.findAllByUserId(userId).stream().map(p -> new ResponsePurchaseDTO(p.getDate(), p.getPrice(), null)).toList();
    }

    public ResponsePurchaseDTO getByIdAndUserId(Long id, Long userId) throws RuntimeException {
        final Purchase purchase = purchaseRepository.findAllByIdAndUserId(id, userId).orElseThrow(EntityNotFoundException::new);
        return new ResponsePurchaseDTO(purchase.getDate(), purchase.getPrice(), purchase.getBookPurchases().stream().map(BookPurchase::getBook).toList());
    }

    public ResponsePurchaseDTO create(AppUser user, RequestPurchaseDTO data) {
        Purchase purchase = new Purchase();
        purchase.setUser(user);

        Set<BookPurchase> bookPurchases = new HashSet<>();
        double price = 0.0;

        for (Long bookId : data.getBookIds()) {
            Book book = bookService.getById(bookId);
            if (book == null || !book.isEbook()) continue;
            bookPurchases.add(BookPurchase.builder().book(book).purchase(purchase).build());
            price += book.getEbookPrice();
        }

        purchase.setPrice(price);
        purchase.setBookPurchases(bookPurchases);

        Purchase savedPurchase = create(purchase);
        mailService.sendEmailAboutPurchase(user.getEmail(), savedPurchase);

        return new ResponsePurchaseDTO(savedPurchase.getDate(), savedPurchase.getPrice(), null);
    }
}
