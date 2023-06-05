package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTests {

    private final BookingService bookingService;
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
        booking = bookingService.create(BookingDto.builder().itemId(1).start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(3)).build(), booker.getId());
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
    public void create_returnsException_itemAvailableFalse() {

        creatingObjects();

        ItemDto newItemDto = itemController.createItem(ItemDto.builder().name("дрель1").description("Простая дрель1")
                .available(false).build(), owner.getId());
        BookingDto newBookingDto = BookingDto.builder().itemId(newItemDto.getId()).start(LocalDateTime.now()
                .plusSeconds(1)).end(LocalDateTime.now().plusSeconds(3)).build();

        assertThrows(BadRequestException.class, () -> bookingService.create(newBookingDto, booker.getId()));
    }

    @DirtiesContext
    @Test
    public void create_returnsException_bookYourItems() {

        creatingObjects();

        ItemDto newItemDto = itemController.createItem(ItemDto.builder().name("дрель1").description("Простая дрель1")
                .available(true).build(), owner.getId());
        BookingDto newBookingDto = BookingDto.builder().itemId(newItemDto.getId()).start(LocalDateTime.now()
                .plusSeconds(1)).end(LocalDateTime.now().plusSeconds(3)).build();

        assertThrows(ObjectNotFoundException.class, () -> bookingService.create(newBookingDto, owner.getId()));
    }

    @DirtiesContext
    @Test
    public void create_returnsException_endIsNull() {

        creatingObjects();

        ItemDto newItemDto = itemController.createItem(ItemDto.builder().name("дрель1").description("Простая дрель1")
                .available(true).build(), owner.getId());
        BookingDto newBookingDto = BookingDto.builder().itemId(newItemDto.getId()).start(LocalDateTime.now()
                .plusSeconds(1)).build();

        assertThrows(BadRequestException.class, () -> bookingService.create(newBookingDto, booker.getId()));
    }

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectBookingDto_underNormalConditions() {

        creatingObjects();

        BookingDto booking1 = bookingService.findById(booking.getId(), booker.getId());

        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.WAITING);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());
    }

    @Test
    public void findById_returnException_invalidId() {

        assertThrows(ObjectNotFoundException.class, () -> bookingService.findById(999, 1));
    }

    @DirtiesContext
    @Test
    public void updateBooking_returnsTheCorrectBookingDto_underAvailableFalse() {

        creatingObjects();

        BookingDto booking1 = bookingService.update(owner.getId(), booking.getId(), false);

        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.REJECTED);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.update(owner.getId(), 999, false));

        assertThrows(BadRequestException.class, () -> bookingService.update(owner.getId(), booking.getId(), true));
    }

    @DirtiesContext
    @Test
    public void updateBooking_returnsTheCorrectBookingDto_underAvailableTrue() {

        creatingObjects();

        BookingDto booking1 = bookingService.update(owner.getId(), booking.getId(), true);

        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.APPROVED);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.update(booker.getId(), booking.getId(), true));
        assertThrows(ObjectNotFoundException.class, () -> bookingService.update(owner.getId(), 999, true));
        assertThrows(BadRequestException.class, () -> bookingService.update(owner.getId(), booking.getId(), false));
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        creatingObjects();

        bookingService.deleteBooking(booking.getId(), booker.getId());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.findById(booking.getId(), booker.getId()));
        assertThrows(ValidateException.class, () -> bookingService.getUsersBooking(booker.getId(), "ALL", 0, 10));
        assertThrows(ValidateException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "ALL", 0, 10));

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "ALL", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "ALL", 0, 0));
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        UserDto user = userController.createUser(UserDto.builder().name("user").email("user@user.com").build());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.findById(999, user.getId()));
        assertThrows(ValidateException.class, () -> bookingService.getUsersBooking(user.getId(), "ALL", 0, 10));
        assertThrows(ValidateException.class, () -> bookingService.getBookingsForUsersItems(user.getId(), "ALL", 0, 10));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUser_returnsTheCorrectBookingDtoList_underNormalConditions() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "ALL", 0, 10);

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
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateALL() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "ALL", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "ALL", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "ALL", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStatePAST() throws InterruptedException {

        creatingObjects();

        TimeUnit.SECONDS.sleep(4);

        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "PAST", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "PAST", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "PAST", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateWAITING() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "WAITING", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "WAITING", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "WAITING", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateREJECTED() {

        creatingObjects();

        BookingDto newBookingDto = bookingService.update(owner.getId(), booking.getId(), false);

        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "REJECTED", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), newBookingDto.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), newBookingDto.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), newBookingDto.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), newBookingDto.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), newBookingDto.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), newBookingDto.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), newBookingDto.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "REJECTED", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "REJECTED", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateFUTURE() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "FUTURE", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "FUTURE", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "FUTURE", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateDefault() {

        creatingObjects();

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "iwugdef", 0, 10));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateCURRENT() throws InterruptedException {

        creatingObjects();

        TimeUnit.SECONDS.sleep(2);

        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "CURRENT", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "CURRENT", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "CURRENT", 0, 0));
    }

    @DirtiesContext
    @Test
    public void findAll_returnsTheCorrectBookingDtoList_underNormalConditions() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingService.findAll();

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
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateALL() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "ALL", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(owner.getId(), "ALL", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(owner.getId(), "ALL", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateDefault() {

        creatingObjects();

        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "iwugdef", 0, 10));
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateFUTURE() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "FUTURE", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "FUTURE", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "FUTURE", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateWAITING() {

        creatingObjects();

        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "WAITING", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "WAITING", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "WAITING", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateREJECTED() {

        creatingObjects();

        BookingDto newBookingDto = bookingService.update(owner.getId(), booking.getId(), false);

        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "REJECTED", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), newBookingDto.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), newBookingDto.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), newBookingDto.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), newBookingDto.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), newBookingDto.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), newBookingDto.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), newBookingDto.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "REJECTED", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "REJECTED", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStatePAST() throws InterruptedException {

        creatingObjects();

        TimeUnit.SECONDS.sleep(4);

        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "PAST", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "PAST", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "PAST", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateCURRENT() throws InterruptedException {

        creatingObjects();

        TimeUnit.SECONDS.sleep(2);

        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "CURRENT", 0, 10);

        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());

        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "CURRENT", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "CURRENT", 0, 0));
    }
}
