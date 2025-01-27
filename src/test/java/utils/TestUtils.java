package utils;

import cz.upce.nnpro.bookbooking.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.entity.*;
import cz.upce.nnpro.bookbooking.entity.enums.GenreE;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class TestUtils {

    public static RegisterRequest testRegisterRequest() {
        return new RegisterRequest("FirstName", "LastName", "test@example.com", "testUser", "password123");
    }

    public static Book testBook() {
        return new Book("Test Book", "Author", GenreE.ACTION, "Description", true, true, true, 5, 3, 20.0);
    }

    public static Book testBook(String title) {
        Book book = testBook();
        book.setTitle(title);
        return book;
    }

    public static Book testBook(String title, Double price) {
        Book book = testBook(title);
        book.setEbookPrice(price);
        return book;
    }

    public static Review testReview(Book book, AppUser user, int rating) {
        return new Review("Review", rating, user, book);
    }

    public static Booking testBooking(Order order, Book book) {
        return new Booking(order, book, 2, false);
    }

    public static Booking testOnlineBooking(Order order, Book book) {
        return new Booking(order, book, 0, true);
    }
}
