package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerId(Integer userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Integer userId, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Integer userId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Integer userId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Integer userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusIn(Integer userId, Set<BookingStatus> rejected, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Integer userId, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?3")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(Integer userId, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.end < ?2")
    List<Booking> findAllByOwnerIdAndEndBefore(Integer userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start > ?2")
    List<Booking> findAllByOwnerIdAndStartAfter(Integer userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findAllByOwnerIdAndStatus(Integer userId, BookingStatus status, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status in ?2")
    List<Booking> findAllByOwnerIdAndStatusIn(Integer userId, Set<BookingStatus> rejected, Pageable pageable);

    @Query(value = "select b from Booking b where b.id = ?1 and (b.item.owner.id = ?2 or b.booker.id =  ?2)")
    Optional<Booking> findBooking(int bookingId, Integer userId);

    List<Booking> findAllByItem(Item item);

    List<Booking> findAllByItemIn(Collection<Item> items);
}

