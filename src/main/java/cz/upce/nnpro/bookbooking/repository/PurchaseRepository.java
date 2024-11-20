package cz.upce.nnpro.bookbooking.repository;

import cz.upce.nnpro.bookbooking.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findAllByUserId(Long userId);

    Optional<Purchase> findAllByIdAndUserId(Long id, Long userId);
}
