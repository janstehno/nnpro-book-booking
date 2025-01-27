package cz.upce.nnpro.bookbooking.repository;

import cz.upce.nnpro.bookbooking.entity.Book;
import cz.upce.nnpro.bookbooking.entity.Booking;
import cz.upce.nnpro.bookbooking.entity.enums.StatusE;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByOrderUserId(Long userId);

    Page<Booking> findByBookAndStatusOrderByOrderDateAsc(Book book, StatusE status, Pageable pageable);

    Page<Booking> findByStatus(StatusE status, Pageable pageable);

    List<Booking> findAllByOrderUserIdAndIdIn(Long userId, List<Long> bookIds);

    Optional<Booking> findByOrderUserIdAndOrderIdAndId(Long userId, Long orderId, Long bookingId);
}
