package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTests {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private final Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());
    private final LocalDateTime dateTime = LocalDateTime.now();
    private final Set<BookingStatus> rejected = Set.of(BookingStatus.REJECTED, BookingStatus.CANCELED);

    private void creatingObjects() {

        owner = userRepository.save(User.builder().id(1).name("user").email("user@user.com").build());
        booker = userRepository.save(User.builder().id(2).name("user1").email("user1@user.com").build());
        item = itemRepository.save(Item.builder().id(1).name("дрель").description("Простая дрель").available(true)
                .owner(owner).build());
        booking = bookingRepository.save(Booking.builder().id(1).start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(30)).booker(booker).item(item).status(BookingStatus.WAITING).build());
    }

    @DirtiesContext
    @Test
    public void findBooking_returnsTheCorrectBooking_underNormalConditions() {

        creatingObjects();

        Booking booking1 = bookingRepository.findBooking(booking.getId(), booker.getId()).get();

        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.WAITING);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());
    }

    @DirtiesContext
    @Test
    public void findBooking_returnException_invalidId() {

        creatingObjects();

        assertTrue(bookingRepository.findBooking(999, booker.getId()).isEmpty());
    }


    @DirtiesContext
    @Test
    public void updateBooking_returnsTheCorrectBooking_underNormalConditions() {

        creatingObjects();

        Booking newBooking = booking = bookingRepository.save(Booking.builder().id(1).start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(30)).booker(booker).item(item).status(BookingStatus.APPROVED).build());

        Booking booking1 = bookingRepository.save(newBooking);

        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.APPROVED);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        creatingObjects();
        bookingRepository.deleteById(booking.getId());

        assertTrue(bookingRepository.findBooking(booking.getId(), booker.getId()).isEmpty());
    }

    @DirtiesContext
    @Test
    public void findAllByBookerId_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        List<Booking> bookingList = bookingRepository.findAllByBookerId(booker.getId(), pageable);

        assertEquals(bookingList.get(0).getId(), booking.getId());
        assertEquals(bookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), booking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByBookerIdAndStartBeforeAndEndAfter_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        Booking newBooking = bookingRepository.save(Booking.builder().id(5).start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusSeconds(30)).booker(booker).item(item).status(BookingStatus.WAITING).build());

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(), dateTime, dateTime, pageable);

        assertEquals(bookingList.get(0).getId(), newBooking.getId());
        assertEquals(bookingList.get(0).getStatus(), newBooking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), newBooking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), newBooking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), newBooking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), newBooking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByBookerIdAndEndBefore_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        Booking newBooking = bookingRepository.save(Booking.builder().id(5).start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(3)).booker(booker).item(item).status(BookingStatus.WAITING).build());

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), dateTime, pageable);

        assertEquals(bookingList.get(0).getId(), newBooking.getId());
        assertEquals(bookingList.get(0).getStatus(), newBooking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), newBooking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), newBooking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), newBooking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), newBooking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByBookerIdAndStartAfter_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), dateTime, pageable);

        assertEquals(bookingList.get(0).getId(), booking.getId());
        assertEquals(bookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), booking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByBookerIdAndStatus_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, pageable);

        assertEquals(bookingList.get(0).getId(), booking.getId());
        assertEquals(bookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), booking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByBookerIdAndStatusIn_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        Booking newBooking = bookingRepository.save(Booking.builder().id(5).start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(3)).booker(booker).item(item).status(BookingStatus.REJECTED).build());

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStatusIn(booker.getId(), rejected, pageable);

        assertEquals(bookingList.get(0).getId(), newBooking.getId());
        assertEquals(bookingList.get(0).getStatus(), newBooking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), newBooking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), newBooking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), newBooking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), newBooking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByOwnerIdAndStatusIn_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        Booking newBooking = bookingRepository.save(Booking.builder().id(5).start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(3)).booker(booker).item(item).status(BookingStatus.REJECTED).build());

        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndStatusIn(owner.getId(), rejected, pageable);

        assertEquals(bookingList.get(0).getId(), newBooking.getId());
        assertEquals(bookingList.get(0).getStatus(), newBooking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), newBooking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), newBooking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), newBooking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), newBooking.getBooker().getId());
    }


    @DirtiesContext
    @Test
    public void findAllByOwnerIdAndStatus_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING, pageable);

        assertEquals(bookingList.get(0).getId(), booking.getId());
        assertEquals(bookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), booking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByOwnerIdAndStartAfter_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndStartAfter(owner.getId(), dateTime, pageable);

        assertEquals(bookingList.get(0).getId(), booking.getId());
        assertEquals(bookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), booking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByOwnerIdAndEndBefore_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        Booking newBooking = bookingRepository.save(Booking.builder().id(5).start(LocalDateTime.now().minusHours(10))
                .end(LocalDateTime.now().minusHours(3)).booker(booker).item(item).status(BookingStatus.WAITING).build());

        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndEndBefore(owner.getId(), dateTime, pageable);

        assertEquals(bookingList.get(0).getId(), newBooking.getId());
        assertEquals(bookingList.get(0).getStatus(), newBooking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), newBooking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), newBooking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), newBooking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), newBooking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByOwnerIdAndStartBeforeAndEndAfter_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        Booking newBooking = bookingRepository.save(Booking.builder().id(5).start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusSeconds(30)).booker(booker).item(item).status(BookingStatus.WAITING).build());
        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(owner.getId(), dateTime, dateTime, pageable);

        assertEquals(bookingList.get(0).getId(), newBooking.getId());
        assertEquals(bookingList.get(0).getStatus(), newBooking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), newBooking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), newBooking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), newBooking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), newBooking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByItemOwnerId_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        List<Booking> bookingList = bookingRepository.findAllByItemOwnerId(owner.getId(), pageable);

        assertEquals(bookingList.get(0).getId(), booking.getId());
        assertEquals(bookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), booking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void findAllByItemIn_returnsTheCorrectBookingList_underNormalConditions() {

        creatingObjects();

        List<Booking> bookingList = bookingRepository.findAllByItemIn(List.of(item));

        assertEquals(bookingList.get(0).getId(), booking.getId());
        assertEquals(bookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingList.get(0).getBooker().getId(), booking.getBooker().getId());
    }
}
