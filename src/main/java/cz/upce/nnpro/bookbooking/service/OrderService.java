package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.OrderDTO;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import cz.upce.nnpro.bookbooking.repository.OrderRepository;
import cz.upce.nnpro.bookbooking.security.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderService implements ServiceInterface<Order> {

    private final OrderRepository orderRepository;

    private final BookService bookService;

    private final MailService mailService;

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order getById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public Order create(AppUser user, OrderDTO data) {
        Order order = new Order();
        order.setUser(user);

        Set<Booking> bookings = new HashSet<>();

        for (Map.Entry<Long, Integer> entry : data.getBooks().entrySet()) {
            Book book = bookService.getById(entry.getKey());
            if (book == null || !book.isPhysical()) continue;

            final int count = entry.getValue();
            Booking booking = Booking.builder().order(order).book(book).count(count).build();

            //TODO expirationDate
            if (book.getAvailableCopies() >= count) {
                booking.setStatus(StatusE.AVAILABLE);
                book.setAvailableCopies(book.getAvailableCopies() - count);
            } else {
                booking.setStatus(StatusE.WAITING);
            }

            bookings.add(booking);
            bookService.update(book);
        }

        order.setBookings(bookings);

        Order savedOrder = orderRepository.save(order);
        mailService.sendEmailAboutOrder(user.getEmail(), savedOrder);

        return savedOrder;
    }

    @Override
    public Order update(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getAllByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public Order getByIdAndUserId(Long id, Long userId) {
        return orderRepository.findByIdAndUserId(id, userId);
    }
}
