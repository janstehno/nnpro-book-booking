package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Order;
import cz.upce.nnpro.bookbooking.repository.OrderRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class OrderService implements ServiceInterface<Order> {

    private final OrderRepository bookRepository;

    @Override
    public List<Order> getAll() {
        return bookRepository.findAll();
    }

    @Override
    public Order getById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public Order create(Order order) {
        return bookRepository.save(order);
    }

    @Override
    public Order update(Order order) {
        return bookRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
