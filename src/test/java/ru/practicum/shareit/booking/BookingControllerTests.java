package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTests {

    private final BookingController bookingController;
    private final ItemController itemController;
    private final UserController userController;
    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private BookingDto booking;

    private void creatingObjects() {

        owner = userController.createUser(UserDto.builder().name("user").email("user@user.com").build());
        booker = userController.createUser(UserDto.builder().name("user1").email("user1@user.com").build());
        item = itemController.createItem(ItemDto.builder().name("дрель").description("Простая дрель").available(true).build(),
                owner.getId());
        booking = bookingController.createBooking(BookingDto.builder().itemId(1).start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(30)).build(), booker.getId());
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectBookingDto_underNormalConditions() {

        creatingObjects();

        assertEquals(booking.getId(), 1);
        assertEquals(booking.getStatus(), BookingStatus.WAITING);
        assertEquals(booking.getBooker().getId(), booker.getId());
        assertEquals(booking.getBooker().getName(), booker.getName());
        assertEquals(booking.getItem().getId(), item.getId());
        assertEquals(booking.getItem().getName(), item.getName());
    }

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectBookingDto_underNormalConditions() {

        creatingObjects();

        BookingDto booking1 = bookingController.findBookingById(booker.getId(), booking.getId());

        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.WAITING);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());
    }

    @Test
    public void findById_returnException_invalidId() {

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingController.findBookingById(1, 999);
        });
    }


    @DirtiesContext
    @Test
    public void updateBooking_returnsTheCorrectBookingDto_underNormalConditions() {

        creatingObjects();

        BookingDto booking1 = bookingController.updateBooking(owner.getId(), booking.getId(), false);

        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.REJECTED);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        creatingObjects();

        bookingController.deleteBooking(booking.getId(), booker.getId());

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingController.findBookingById(booker.getId(), booking.getId());
        });
        assertThrows(ValidateException.class, () -> {
            bookingController.getBookingsForUser(booker.getId(), "ALL", 0, 10);
        });
        assertThrows(ValidateException.class, () -> {
            bookingController.getBookingsForUsersItems(owner.getId(), "ALL", 0, 10);
        });
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        UserDto user = userController.createUser(UserDto.builder().name("user").email("user@user.com").build());

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingController.findBookingById(user.getId(), 999);
        });
        assertThrows(ValidateException.class, () -> {
            bookingController.getBookingsForUser(user.getId(), "ALL", 0, 10);
        });
        assertThrows(ValidateException.class, () -> {
            bookingController.getBookingsForUsersItems(user.getId(), "ALL", 0, 10);
        });
    }

    @DirtiesContext
    @Test
    public void getBookingsForUser_returnsTheCorrectBookingDtoList_underNormalConditions() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingController.getBookingsForUser(booker.getId(), "ALL", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditions() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingController.getBookingsForUsersItems(owner.getId(), "ALL", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());
    }
}
