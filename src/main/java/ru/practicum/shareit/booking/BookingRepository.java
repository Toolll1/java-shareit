package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime, LocalDateTime dateTime1);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer userId, BookingStatus status);

    List<Booking> findAllByBookerIdAndStatusInOrderByStartDesc(Integer userId, Set<BookingStatus> rejected);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer userId);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?3 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime, LocalDateTime dateTime1);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Integer userId, BookingStatus status);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status in ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusInOrderByStartDesc(Integer userId, Set<BookingStatus> rejected);

    @Query(value = "select b from Booking b where b.id = ?1 and (b.item.owner.id = ?2 or b.booker.id =  ?2)")
    Optional<Booking> findBooking(int bookingId, Integer userId);

    List<Booking> findAllByItem(Item item);

    List<Booking> findAllByItemIn(Collection<Item> items);
}

