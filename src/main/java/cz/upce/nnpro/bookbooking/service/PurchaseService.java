package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.PurchaseDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.join.BookPurchase;
import cz.upce.nnpro.bookbooking.repository.PurchaseRepository;
import cz.upce.nnpro.bookbooking.security.service.MailService;
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
    public Purchase getById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    @Override
    public Purchase create(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public Purchase create(AppUser user, PurchaseDTO data) {
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

        Purchase savedPurchase = purchaseRepository.save(purchase);
        mailService.sendEmailAboutPurchase(user.getEmail(), savedPurchase);

        return savedPurchase;
    }

    @Override
    public Purchase update(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public void deleteById(Long id) {
        purchaseRepository.deleteById(id);
    }

    public List<Purchase> getAllByUserId(Long userId) {
        return purchaseRepository.findAllByUserId(userId);
    }

    public Purchase getByIdAndUserId(Long id, Long userId) {
        return purchaseRepository.findAllByIdAndUserId(id, userId);
    }
}
