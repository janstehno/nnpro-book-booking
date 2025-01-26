package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.RequestPurchaseDTO;
import cz.upce.nnpro.bookbooking.dto.ResponsePurchaseDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.entity.join.BookPurchase;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.PurchaseRepository;
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
        return purchaseRepository.findById(id).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
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
        return purchaseRepository.findAllByUserId(userId).stream().map(ResponsePurchaseDTO::new).toList();
    }

    public ResponsePurchaseDTO getByIdAndUserId(Long id, Long userId) throws RuntimeException {
        final Purchase purchase = purchaseRepository.findAllByIdAndUserId(id, userId).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
        return new ResponsePurchaseDTO(purchase);
    }

    public ResponsePurchaseDTO create(AppUser user, RequestPurchaseDTO data) {
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase = create(purchase);

        Set<BookPurchase> bookPurchases = new HashSet<>();
        double price = 0.0;

        for (Long bookId : data.getBookIds()) {
            Book book = bookService.getById(bookId);
            if (book == null || !book.isEbook()) continue;

            bookPurchases.add(new BookPurchase(book, purchase));
            price += book.getEbookPrice();
        }

        purchase.setPrice(price);
        purchase.setBookPurchases(bookPurchases);
        Purchase savedPurchase = purchaseRepository.save(purchase);

        mailService.sendEmailAboutPurchase(user.getEmail(), savedPurchase);

        return new ResponsePurchaseDTO(savedPurchase);
    }
}
