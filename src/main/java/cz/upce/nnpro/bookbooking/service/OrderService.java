package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.RequestOrderDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseOrderDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderService implements ServiceInterface<Order> {

    private final OrderRepository orderRepository;

    private final BookService bookService;

    private final BookingService bookingService;

    private final MailService mailService;

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order getById(Long id) throws RuntimeException {
        return orderRepository.findById(id).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }

    @Override
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order update(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public List<ResponseOrderDTO> getAllByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId).stream().map(ResponseOrderDTO::new).toList();
    }

    public ResponseOrderDTO getByIdAndUserId(Long id, Long userId) throws RuntimeException {
        final Order order = orderRepository.findByIdAndUserId(id, userId).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
        return new ResponseOrderDTO(order);
    }

    @Transactional
    public ResponseOrderDTO create(AppUser user, List<RequestOrderDTO> data) {
        Order order = new Order();
        order.setUser(user);
        order = create(order);

        Set<Booking> bookings = new HashSet<>(createBookings(order, data));

        order.setBookings(bookingService.createAll(bookings));
        Order savedOrder = orderRepository.save(order);

        mailService.sendEmailAboutOrder(user.getEmail(), savedOrder);

        return new ResponseOrderDTO(savedOrder);
    }

    private Set<Booking> createBookings(Order order, List<RequestOrderDTO> data) {
        Set<Booking> bookings = new HashSet<>();

        for (RequestOrderDTO d : data) {
            Book book = bookService.getById(d.getId());
            if (book == null || !book.isPhysical()) continue;

            boolean online = d.isOnline();
            int count = d.getCount();

            if (d.isOnline()) {
                count = 0;
                if (!book.isOnline()) online = false;
            } else {
                if (count <= 0) continue;
                if (count > book.getPhysicalCopies()) count = book.getPhysicalCopies();
            }

            Booking booking = new Booking(order, book, count, online);

            bookingService.handleReservation(booking);

            bookings.add(booking);
            bookService.update(book);
        }

        return bookings;
    }
}
