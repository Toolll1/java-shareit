package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTests {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final ItemRequestService itemRequestService;

    private UserDto userDto;
    private UserDto userDto2;
    private ItemDto itemDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {

        userDto = userService.createUser(UserDto.builder().name("user").email("user@user.com").build());
        userDto2 = userService.createUser(UserDto.builder().name("user1").email("user1@user.com").build());
        itemRequestDto = itemRequestService.createRequest(ItemRequestDto.builder()
                .description("Хотел бы воспользоваться щёткой для обуви").build(), userDto2.getId());
        itemDto = itemService.createItem(ItemDto.builder().name("дрель").description("Простая дрель")
                .requestId(itemRequestDto.getId()).available(true).build(), userDto.getId());
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectItemDto_underNormalConditions() {

        assertEquals(itemDto.getId(), 1);
        assertEquals(itemDto.getName(), "дрель");
        assertEquals(itemDto.getDescription(), "Простая дрель");
        assertEquals(itemDto.getAvailable(), true);
        assertEquals(itemDto.getOwner().getId(), userDto.getId());
    }


    @DirtiesContext
    @Test
    public void update_returnsTheCorrectItemDto_underNormalConditions() {

        ItemDto itemDto2 = itemService.updateItem(ItemDto.builder().name("новая дрель").id(itemDto.getId())
                .requestId(itemRequestDto.getId()).description("Простая новая дрель").available(false).build(), userDto.getId());

        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(ItemDto.builder().name("новая дрель")
                .id(itemDto.getId()).description("Простая новая дрель").available(false).build(), userDto2.getId()));

        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(ItemDto.builder().id(itemDto.getId())
                .name("новая дрель").description("Простая новая дрель").available(false).build(), 999));
        assertEquals(itemDto2.getId(), 1);
        assertEquals(itemDto2.getName(), "новая дрель");
        assertEquals(itemDto2.getDescription(), "Простая новая дрель");
        assertEquals(itemDto2.getAvailable(), false);
        assertEquals(itemDto2.getOwner().getId(), userDto.getId());
        assertEquals(itemDto2.getRequestId(), itemRequestDto.getId());
    }

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectItemDto_underNormalConditions() {

        ItemDto itemDto2 = itemService.findItemById(itemDto.getId(), userDto.getId());

        assertEquals(itemDto2.getId(), itemDto.getId());
        assertEquals(itemDto2.getName(), itemDto.getName());
        assertEquals(itemDto2.getDescription(), itemDto.getDescription());
        assertEquals(itemDto2.getAvailable(), itemDto.getAvailable());
        assertEquals(itemDto2.getOwner().getId(), userDto.getId());
    }

    @DirtiesContext
    @Test
    public void findAllByOwnerId_returnsTheCorrectItemDtoList_underNormalConditions() {

        ItemDto itemDto2 = itemService.createItem(ItemDto.builder().name("новая дрель").description("Простая новая дрель")
                .available(false).build(), userDto.getId());
        BookingDto booking1 = bookingService.createBooking(BookingDto.builder().itemId(itemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1)).end(LocalDateTime.now().plusSeconds(3)).build(), userDto2.getId());
        List<ItemDto> itemDtoList1 = itemService.findAllByOwnerId(userDto.getId(), 0, 10);
        List<ItemDto> itemDtoList2 = itemService.findAllByOwnerId(userDto.getId(), 1, 1);

        assertEquals(itemDtoList1.size(), 2);
        assertEquals(itemDtoList1.get(0).getId(), itemDto.getId());
        assertEquals(itemDtoList1.get(0).getName(), itemDto.getName());
        assertEquals(itemDtoList1.get(0).getDescription(), itemDto.getDescription());
        assertEquals(itemDtoList1.get(0).getAvailable(), itemDto.getAvailable());
        assertEquals(itemDtoList1.get(0).getOwner().getId(), userDto.getId());
        assertNull(itemDtoList1.get(0).getLastBooking());
        assertEquals(itemDtoList1.get(0).getNextBooking().getId(), booking1.getId());
        assertEquals(itemDtoList2.size(), 1);
        assertEquals(itemDtoList2.get(0).getId(), itemDto2.getId());
        assertEquals(itemDtoList2.get(0).getName(), itemDto2.getName());
        assertEquals(itemDtoList2.get(0).getDescription(), itemDto2.getDescription());
        assertEquals(itemDtoList2.get(0).getAvailable(), itemDto2.getAvailable());
        assertEquals(itemDtoList2.get(0).getOwner().getId(), userDto.getId());
    }


    @DirtiesContext
    @Test
    public void createComment_returnsTheCorrectCommentDto_underNormalConditions() throws InterruptedException {

        bookingService.createBooking(
                BookingDto.builder()
                        .itemId(itemDto.getId())
                        .start(LocalDateTime.now().plusSeconds(1))
                        .end(LocalDateTime.now().plusSeconds(30))
                        .build(),
                userDto2.getId());

        assertThrows(BadRequestException.class, () -> itemService.createComment(CommentDto.builder()
                .text("текст комментария").created(LocalDateTime.now()).build(), userDto2.getId(), itemDto.getId()));

        TimeUnit.SECONDS.sleep(2);

        CommentDto commentDto = itemService.createComment(CommentDto.builder().text("текст комментария")
                .created(LocalDateTime.now()).build(), userDto2.getId(), itemDto.getId());

        assertEquals(commentDto.getId(), 1);
        assertEquals(commentDto.getItemId(), itemDto.getId());
        assertEquals(commentDto.getText(), "текст комментария");
        assertEquals(commentDto.getAuthorId(), userDto2.getId());
        assertEquals(commentDto.getAuthorName(), userDto2.getName());
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        assertThrows(ValidateException.class, () -> itemService.deleteItem(itemDto.getId(), 999));

        itemService.deleteItem(itemDto.getId(), userDto.getId());

        assertEquals(itemService.findAllByOwnerId(userDto.getId(), 0, 10).size(), 0);
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        assertThrows(ObjectNotFoundException.class, () -> itemService.deleteItem(999, userDto.getId()));
        assertThrows(ObjectNotFoundException.class, () -> itemService.findItemById(999, userDto.getId()));
    }

    @DirtiesContext
    @Test
    public void search_returnsTheCorrectList_underNormalConditions() {

        ItemDto itemDto2 = itemService.createItem(ItemDto.builder().name("дрель V100500").description("Непростая дрель")
                .available(true).build(), userDto.getId());

        List<ItemDto> itemDtoList1 = itemService.searchItems("дрель", 0, 10);
        List<ItemDto> itemDtoList2 = itemService.searchItems("дрель", 1, 1);
        List<ItemDto> itemDtoList3 = itemService.searchItems("", 0, 10);

        assertEquals(itemDtoList1.size(), 2);
        assertEquals(itemDtoList1.get(0).getId(), itemDto.getId());
        assertEquals(itemDtoList1.get(0).getName(), itemDto.getName());
        assertEquals(itemDtoList1.get(0).getDescription(), itemDto.getDescription());
        assertEquals(itemDtoList1.get(0).getAvailable(), itemDto.getAvailable());
        assertEquals(itemDtoList1.get(0).getOwner().getId(), userDto.getId());
        assertEquals(itemDtoList2.size(), 1);
        assertEquals(itemDtoList2.get(0).getId(), itemDto2.getId());
        assertEquals(itemDtoList2.get(0).getName(), itemDto2.getName());
        assertEquals(itemDtoList2.get(0).getDescription(), itemDto2.getDescription());
        assertEquals(itemDtoList2.get(0).getAvailable(), itemDto2.getAvailable());
        assertEquals(itemDtoList2.get(0).getOwner().getId(), userDto.getId());
        assertEquals(itemDtoList3.size(), 0);
    }


    @DirtiesContext
    @Test
    public void addData_returnsTheCorrectItemDto_underNormalConditions() throws InterruptedException {

        ItemDto itemDto2 = itemService.addData(itemMapper.dtoToObject(itemDto, userDto.getId()), new ArrayList<>());

        assertEquals(itemDto2.getId(), itemDto.getId());
        assertNull(itemDto2.getLastBooking());
        assertNull(itemDto2.getNextBooking());
        assertEquals(itemDto2.getName(), itemDto.getName());
        assertEquals(itemDto2.getAvailable(), itemDto.getAvailable());
        assertEquals(itemDto2.getDescription(), itemDto.getDescription());
        assertEquals(itemDto2.getOwner().getId(), itemDto.getOwner().getId());
        assertEquals(itemDto2.getRequestId(), itemDto.getRequestId());
        assertEquals(itemDto2.getComments(), new ArrayList<>());

        Booking booking1 = bookingMapper.dtoToObject(bookingService.createBooking(BookingDto.builder().itemId(itemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1)).end(LocalDateTime.now().plusSeconds(5)).build(), userDto2.getId()));
        Booking booking2 = bookingMapper.dtoToObject(bookingService.createBooking(BookingDto.builder().itemId(itemDto.getId())
                .start(LocalDateTime.now().plusSeconds(3)).end(LocalDateTime.now().plusSeconds(5)).build(), userDto2.getId()));

        TimeUnit.SECONDS.sleep(2);

        ItemDto itemDto3 = itemService.addData(itemMapper.dtoToObject(itemDto, userDto.getId()), List.of(booking1, booking2));

        assertEquals(itemDto3.getId(), itemDto.getId());
        assertEquals(itemDto3.getLastBooking().getId(), booking1.getId());
        assertEquals(itemDto3.getNextBooking().getId(), booking2.getId());
    }
}
