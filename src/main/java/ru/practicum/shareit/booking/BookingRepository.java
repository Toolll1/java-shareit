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

    List<Booking> findAllByBookerIdAndBookingStatusOrderByStartDesc(Integer userId, BookingStatus bookingStatus);

    List<Booking> findAllByBookerIdAndBookingStatusInOrderByStartDesc(Integer userId, Set<BookingStatus> rejected);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Integer userId);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?3 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime, LocalDateTime dateTime1);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(Integer userId, LocalDateTime dateTime);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.bookingStatus = ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndBookingStatusOrderByStartDesc(Integer userId, BookingStatus bookingStatus);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.bookingStatus in ?2 order by b.start desc")
    List<Booking> findAllByOwnerIdAndBookingStatusInOrderByStartDesc(Integer userId, Set<BookingStatus> rejected);

    @Query(value = "select b from Booking b where b.id = ?1 and (b.item.owner.id = ?2 or b.booker.id =  ?2)")
    Optional<Booking> findBooking(int bookingId, Integer userId);

    List<Booking> findAllByItem(Item item);

    List<Booking> findAllByItemIn(Collection<Item> items);
}

