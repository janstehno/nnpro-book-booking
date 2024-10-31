package cz.upce.nnpro.bookbooking.service;

import java.util.List;

public interface ServiceInterface<I> {
    List<I> getAll();

    I getById(Long id);

    I create(I i);

    I update(I i);

    void deleteById(Long id);
}
