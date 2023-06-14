package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

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
    private final ItemService itemService;
    private final UserService userService;
    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private BookingDto booking;

    @BeforeEach
    void beforeEach() {

        owner = userService.createUser(UserDto.builder().name("user").email("user@user.com").build());
        booker = userService.createUser(UserDto.builder().name("user1").email("user1@user.com").build());
        item = itemService.createItem(ItemDto.builder().name("дрель").description("Простая дрель").available(true).build(),
                owner.getId());
        booking = bookingService.createBooking(BookingDto.builder().itemId(1).start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(3)).build(), booker.getId());
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectBookingDto_underNormalConditions() {

        //then
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

        //given
        ItemDto newItemDto = itemService.createItem(ItemDto.builder().name("дрель1").description("Простая дрель1")
                .available(false).build(), owner.getId());
        BookingDto newBookingDto = BookingDto.builder().itemId(newItemDto.getId()).start(LocalDateTime.now()
                .plusSeconds(1)).end(LocalDateTime.now().plusSeconds(3)).build();

        //then
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(newBookingDto, booker.getId()));
    }

    @DirtiesContext
    @Test
    public void create_returnsException_bookYourItems() {

        //given
        ItemDto newItemDto = itemService.createItem(ItemDto.builder().name("дрель1").description("Простая дрель1")
                .available(true).build(), owner.getId());
        BookingDto newBookingDto = BookingDto.builder().itemId(newItemDto.getId()).start(LocalDateTime.now()
                .plusSeconds(1)).end(LocalDateTime.now().plusSeconds(3)).build();

        //then
        assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(newBookingDto, owner.getId()));
    }

    @DirtiesContext
    @Test
    public void create_returnsException_endIsNull() {

        //given
        ItemDto newItemDto = itemService.createItem(ItemDto.builder().name("дрель1").description("Простая дрель1")
                .available(true).build(), owner.getId());
        BookingDto newBookingDto = BookingDto.builder().itemId(newItemDto.getId()).start(LocalDateTime.now()
                .plusSeconds(1)).build();

        //then
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(newBookingDto, booker.getId()));
    }

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectBookingDto_underNormalConditions() {

        //when
        BookingDto booking1 = bookingService.findBookingById(booking.getId(), booker.getId());

        //then
        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.WAITING);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());
    }

    @DirtiesContext
    @Test
    public void findById_returnException_invalidId() {

        //then
        assertThrows(ObjectNotFoundException.class, () -> bookingService.findBookingById(999, 1));
    }

    @DirtiesContext
    @Test
    public void updateBooking_returnsTheCorrectBookingDto_underAvailableFalse() {

        //when
        BookingDto booking1 = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        //then
        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.REJECTED);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());
    }

    @DirtiesContext
    @Test
    public void updateBooking_returnsTheCorrectBookingDto_underAvailableTrue() {

        //when
        BookingDto booking1 = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        //then
        assertEquals(booking1.getId(), 1);
        assertEquals(booking1.getStatus(), BookingStatus.APPROVED);
        assertEquals(booking1.getBooker().getId(), booker.getId());
        assertEquals(booking1.getBooker().getName(), booker.getName());
        assertEquals(booking1.getItem().getId(), item.getId());
        assertEquals(booking1.getItem().getName(), item.getName());
    }

    @DirtiesContext
    @Test
    public void updateBooking_returnException_underIncorrectData() {

        //then
        assertThrows(ObjectNotFoundException.class, () -> bookingService.updateBooking(booker.getId(), booking.getId(), true));
        assertThrows(ObjectNotFoundException.class, () -> bookingService.updateBooking(owner.getId(), 999, true));
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        //when
        bookingService.deleteBooking(booking.getId(), booker.getId());

        //then
        assertThrows(ObjectNotFoundException.class, () -> bookingService.findBookingById(booking.getId(), booker.getId()));
        assertThrows(ValidateException.class, () -> bookingService.getUsersBooking(booker.getId(), "ALL", 0, 10));
        assertThrows(ValidateException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "ALL", 0, 10));
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        //then
        assertThrows(ObjectNotFoundException.class, () -> bookingService.findBookingById(999, booker.getId()));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUser_returnsTheCorrectBookingDtoList_underNormalConditions() {

        //when
        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "ALL", 0, 10);

        //then
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
    public void getBookingsForUsersItems_returnException_underIncorrectFromOpSize() {

        //then
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "ALL", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "ALL", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateALL() {

        //when
        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "ALL", 0, 10);

        //then
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
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStatePAST() throws InterruptedException {

        //given
        TimeUnit.SECONDS.sleep(4);

        //when
        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "PAST", 0, 10);

        //then
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
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateWAITING() {

        //when
        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "WAITING", 0, 10);

        //then
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
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateREJECTED() {

        //given
        BookingDto newBookingDto = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        //when
        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "REJECTED", 0, 10);

        //then
        assertEquals(bookingDtoList.get(0).getId(), newBookingDto.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), newBookingDto.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), newBookingDto.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), newBookingDto.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), newBookingDto.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), newBookingDto.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), newBookingDto.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateFUTURE() {

        //when
        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "FUTURE", 0, 10);

        //then
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
    public void getBookingsForUsersItems_returnException_underNormalConditionsStateDefault() {

        //then
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsForUsersItems(owner.getId(), "iwugdef", 0, 10));
    }

    @DirtiesContext
    @Test
    public void getBookingsForUsersItems_returnsTheCorrectBookingDtoList_underNormalConditionsStateCURRENT() throws InterruptedException {

        //given
        TimeUnit.SECONDS.sleep(2);

        //when
        List<BookingDto> bookingDtoList = bookingService.getBookingsForUsersItems(owner.getId(), "CURRENT", 0, 10);

        //then
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
    public void findAll_returnsTheCorrectBookingDtoList_underNormalConditions() {

        //when
        List<BookingDto> bookingDtoList = bookingService.findAll();

        //then
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

        //when
        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "ALL", 0, 10);

        //then
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
    public void getUsersBooking_returnException_underNormalConditionsStateDefault() {

        //then
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "iwugdef", 0, 10));
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnException_underIncorrectFromOpSize() {

        //then
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "ALL", -1, 10));
        assertThrows(BadRequestException.class, () -> bookingService.getUsersBooking(booker.getId(), "ALL", 0, 0));
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateFUTURE() {

        //when
        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "FUTURE", 0, 10);

        //then
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
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateWAITING() {

        //when
        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "WAITING", 0, 10);

        //then
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
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateREJECTED() {

        //given
        BookingDto newBookingDto = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        //when
        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "REJECTED", 0, 10);

        //then
        assertEquals(bookingDtoList.get(0).getId(), newBookingDto.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), newBookingDto.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), newBookingDto.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), newBookingDto.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), newBookingDto.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), newBookingDto.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), newBookingDto.getBooker().getId());
    }

    @DirtiesContext
    @Test
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStatePAST() throws InterruptedException {

        //given
        TimeUnit.SECONDS.sleep(4);

        //when
        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "PAST", 0, 10);

        //then
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
    public void getUsersBooking_returnsTheCorrectBookingDtoList_underNormalConditionsStateCURRENT() throws InterruptedException {

        //given
        TimeUnit.SECONDS.sleep(2);

        //when
        List<BookingDto> bookingDtoList = bookingService.getUsersBooking(booker.getId(), "CURRENT", 0, 10);

        //then
        assertEquals(bookingDtoList.get(0).getId(), booking.getId());
        assertEquals(bookingDtoList.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDtoList.get(0).getEnd().getSecond(), booking.getEnd().getSecond());
        assertEquals(bookingDtoList.get(0).getStart().getSecond(), booking.getStart().getSecond());
        assertEquals(bookingDtoList.get(0).getItemId(), booking.getItemId());
        assertEquals(bookingDtoList.get(0).getItem().getId(), booking.getItem().getId());
        assertEquals(bookingDtoList.get(0).getBooker().getId(), booking.getBooker().getId());
    }
}
