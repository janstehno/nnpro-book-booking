package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.RequestOrderDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseOrderDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.OrderRepository;
import cz.upce.nnpro.bookbooking.security.RedisService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class OrderService implements ServiceInterface<Order> {

    private final OrderRepository orderRepository;

    private final BookService bookService;

    private final BookingService bookingService;

    private final MailService mailService;

    private final RedisService redisService;

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
        if (bookings.isEmpty()) {
            deleteById(order.getId());
            return null;
        }

        order.setBookings(bookingService.createAll(bookings));
        orderRepository.save(order);

        mailService.sendEmailAboutOrder(user.getEmail(), order);

        return new ResponseOrderDTO(order);
    }

    private Set<Booking> createBookings(Order order, List<RequestOrderDTO> data) {
        Set<Booking> bookings = new HashSet<>();

        for (RequestOrderDTO d : data) {
            RLock lock = redisService.getLock("create-booking-book:" + d.getId());
            if (!redisService.tryLock(lock, 10, TimeUnit.SECONDS)) continue;

            try {
                Booking booking = createBooking(order, d);
                if (booking != null) bookings.add(booking);
            } finally {
                redisService.releaseLock(lock);
            }
        }

        return bookings;
    }

    private Booking createBooking(Order order, RequestOrderDTO request) {
        Book book = bookService.getById(request.getId());
        if (book == null || !book.isPhysical()) return null;

        int count = determineCount(book, request);
        if (count < 0) return null;

        Booking booking = new Booking(order, book, count, request.isOnline());
        bookingService.handleReservation(booking);

        book.setAvailableCopies(book.getAvailableCopies() - count);
        bookService.update(book);

        return booking;
    }

    private int determineCount(Book book, RequestOrderDTO request) {
        if (request.isOnline()) {
            return book.isOnline() ? 0 : -1;
        } else {
            if (request.getCount() <= 0) return -1;
            return Math.min(request.getCount(), book.getPhysicalCopies());
        }
    }

}
