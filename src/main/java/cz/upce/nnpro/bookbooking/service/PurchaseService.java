package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.RequestPurchaseDTO;
import cz.upce.nnpro.bookbooking.dto.ResponsePurchaseDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
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
        final Purchase purchase = purchaseRepository.findByIdAndUserId(id, userId).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
        return new ResponsePurchaseDTO(purchase);
    }

    public ResponsePurchaseDTO create(AppUser user, List<RequestPurchaseDTO> data) {
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase = create(purchase);

        Set<BookPurchase> bookPurchases = new HashSet<>(createBookPurchases(purchase, data));
        if (bookPurchases.isEmpty()) {
            deleteById(purchase.getId());
            return null;
        }

        purchase.setBookPurchases(bookPurchases);
        purchaseRepository.save(purchase);

        mailService.sendEmailAboutPurchase(user.getEmail(), purchase);

        return new ResponsePurchaseDTO(purchase);
    }

    private Set<BookPurchase> createBookPurchases(Purchase purchase, List<RequestPurchaseDTO> data) {
        Set<BookPurchase> bookPurchases = new HashSet<>();

        for (RequestPurchaseDTO d : data) {
            Book book = bookService.getById(d.getId());
            if (book == null || !book.isEbook() || d.getCount() <= 0) continue;

            int count = d.getCount();
            purchase.setPrice(purchase.getPrice() + (book.getEbookPrice() * count));

            bookPurchases.add(new BookPurchase(book, purchase, count));
        }

        return bookPurchases;
    }
}
