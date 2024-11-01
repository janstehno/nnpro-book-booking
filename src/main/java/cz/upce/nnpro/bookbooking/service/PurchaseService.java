package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Purchase;
import cz.upce.nnpro.bookbooking.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseService implements ServiceInterface<Purchase> {

    private final PurchaseRepository purchaseRepository;

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

    @Override
    public Purchase update(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public void deleteById(Long id) {
        purchaseRepository.deleteById(id);
    }
}
