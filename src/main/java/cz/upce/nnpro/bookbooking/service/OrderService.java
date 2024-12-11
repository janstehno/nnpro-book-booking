package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.RequestOrderDTO;
import cz.upce.nnpro.bookbooking.dto.ResponseBookingDTO;
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
import java.util.Map;
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
        return orderRepository.findAllByUserId(userId).stream().map(o -> new ResponseOrderDTO(o.getId(), o.getDate(), null)).toList();
    }

    public ResponseOrderDTO getByIdAndUserId(Long id, Long userId) throws RuntimeException {
        final Order order = orderRepository.findByIdAndUserId(id, userId).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
        return new ResponseOrderDTO(order.getId(), order.getDate(),
                                    order.getBookings()
                                         .stream()
                                         .map(b -> new ResponseBookingDTO(b.getId(),
                                                                          b.getBook(),
                                                                          b.getCount(),
                                                                          b.getStatus(),
                                                                          b.getBookingDate(),
                                                                          b.getExpirationDate()))
                                         .toList());
    }

    @Transactional
    public ResponseOrderDTO create(AppUser user, RequestOrderDTO data) {
        Order order = new Order();
        order.setUser(user);
        order = create(order);

        Set<Booking> bookings = new HashSet<>();

        for (Map.Entry<Long, Integer> entry : data.getBooks().entrySet()) {
            Book book = bookService.getById(entry.getKey());
            if (book == null || !book.isPhysical()) continue;

            final int count = entry.getValue();
            Booking booking = Booking.builder().order(order).book(book).count(count).build();

            bookingService.handleReservation(booking);

            bookings.add(booking);
            bookService.update(book);
        }

        order.setBookings(bookingService.createAll(bookings));
        Order savedOrder = orderRepository.save(order);

        mailService.sendEmailAboutOrder(user.getEmail(), savedOrder);

        return new ResponseOrderDTO(savedOrder.getId(), savedOrder.getDate(), null);
    }
}
