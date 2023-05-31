package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
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
public class ItemControllerTests {

    private final ItemController itemController;
    private final UserController userController;
    private final BookingController bookingController;
    private final UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
    private final ItemDto itemDto = ItemDto.builder().name("дрель").description("Простая дрель").available(true).build();


    @DirtiesContext
    @Test
    public void create_returnsTheCorrectItemDto_underNormalConditions() {

        UserDto newUserDto = userController.createUser(userDto);
        ItemDto itemDto1 = itemController.createItem(itemDto, newUserDto.getId());

        assertEquals(itemDto1.getId(), 1);
        assertEquals(itemDto1.getName(), itemDto.getName());
        assertEquals(itemDto1.getDescription(), itemDto.getDescription());
        assertEquals(itemDto1.getAvailable(), itemDto.getAvailable());
        assertEquals(itemDto1.getOwner().getId(), newUserDto.getId());
    }

    @DirtiesContext
    @Test
    public void update_returnsTheCorrectItemDto_underNormalConditions() {

        UserDto newUserDto = userController.createUser(userDto);
        ItemDto itemDto1 = itemController.createItem(itemDto, newUserDto.getId());
        ItemDto itemDto2 = itemController.updateItem(itemDto1.getId(), ItemDto.builder()
                .name("новая дрель").description("Простая новая дрель").available(false).build(), newUserDto.getId());

        assertEquals(itemDto2.getId(), 1);
        assertEquals(itemDto2.getName(), "новая дрель");
        assertEquals(itemDto2.getDescription(), "Простая новая дрель");
        assertEquals(itemDto2.getAvailable(), false);
        assertEquals(itemDto2.getOwner().getId(), newUserDto.getId());
    }

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectItemDto_underNormalConditions() {

        UserDto newUserDto = userController.createUser(userDto);
        ItemDto itemDto1 = itemController.createItem(itemDto, newUserDto.getId());
        ItemDto itemDto2 = itemController.findItemById(itemDto1.getId(), newUserDto.getId());

        assertEquals(itemDto2.getId(), itemDto1.getId());
        assertEquals(itemDto2.getName(), itemDto1.getName());
        assertEquals(itemDto2.getDescription(), itemDto1.getDescription());
        assertEquals(itemDto2.getAvailable(), itemDto1.getAvailable());
        assertEquals(itemDto2.getOwner().getId(), newUserDto.getId());
    }

    @DirtiesContext
    @Test
    public void findAllByOwnerId_returnsTheCorrectItemDtoList_underNormalConditions() {

        UserDto newUserDto = userController.createUser(userDto);
        ItemDto itemDto1 = itemController.createItem(itemDto, newUserDto.getId());
        ItemDto itemDto2 = itemController.createItem(
                ItemDto.builder().name("новая дрель").description("Простая новая дрель").available(false).build(),
                newUserDto.getId());
        List<ItemDto> itemDtoList1 = itemController.findAllItemsByOwnerId(newUserDto.getId(), 0, 10);
        List<ItemDto> itemDtoList2 = itemController.findAllItemsByOwnerId(newUserDto.getId(), 1, 1);

        assertEquals(itemDtoList1.size(), 2);
        assertEquals(itemDtoList1.get(0).getId(), itemDto1.getId());
        assertEquals(itemDtoList1.get(0).getName(), itemDto1.getName());
        assertEquals(itemDtoList1.get(0).getDescription(), itemDto1.getDescription());
        assertEquals(itemDtoList1.get(0).getAvailable(), itemDto1.getAvailable());
        assertEquals(itemDtoList1.get(0).getOwner().getId(), newUserDto.getId());
        assertEquals(itemDtoList2.size(), 1);
        assertEquals(itemDtoList2.get(0).getId(), itemDto2.getId());
        assertEquals(itemDtoList2.get(0).getName(), itemDto2.getName());
        assertEquals(itemDtoList2.get(0).getDescription(), itemDto2.getDescription());
        assertEquals(itemDtoList2.get(0).getAvailable(), itemDto2.getAvailable());
        assertEquals(itemDtoList2.get(0).getOwner().getId(), newUserDto.getId());
    }

    @DirtiesContext
    @Test
    public void createComment_returnsTheCorrectCommentDto_underNormalConditions() throws InterruptedException {

        UserDto newUserDto = userController.createUser(userDto);
        UserDto newUserDtoForBooking = userController.createUser(
                UserDto.builder().name("UserUserivi4").email("UserUserivi4@user.com").build());
        ItemDto itemDto1 = itemController.createItem(itemDto, newUserDto.getId());

        bookingController.createBooking(
                BookingDto.builder()
                        .itemId(itemDto1.getId())
                        .start(LocalDateTime.now().plusSeconds(1))
                        .end(LocalDateTime.now().plusSeconds(30))
                        .build(),
                newUserDtoForBooking.getId());

        assertThrows(BadRequestException.class, () -> {
            itemController.createComment(CommentDto.builder().text("текст комментария").build(),
                    newUserDtoForBooking.getId(), itemDto1.getId());
        }, "You can't add a comment to an item you didn't book");


        TimeUnit.SECONDS.sleep(2);

        CommentDto commentDto = itemController.createComment(CommentDto.builder().text("текст комментария").build(),
                newUserDtoForBooking.getId(), itemDto1.getId());

        assertEquals(commentDto.getId(), 1);
        assertEquals(commentDto.getItemId(), itemDto1.getId());
        assertEquals(commentDto.getText(), "текст комментария");
        assertEquals(commentDto.getAuthorId(), newUserDtoForBooking.getId());
        assertEquals(commentDto.getAuthorName(), newUserDtoForBooking.getName());
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        UserDto newUserDto = userController.createUser(userDto);
        ItemDto itemDto1 = itemController.createItem(itemDto, newUserDto.getId());

        itemController.deleteItem(itemDto1.getId(), newUserDto.getId());

        assertEquals(itemController.findAllItemsByOwnerId(newUserDto.getId(), 0, 10).size(), 0);
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        UserDto newUserDto = userController.createUser(userDto);

        assertThrows(ObjectNotFoundException.class, () -> {
            itemController.deleteItem(1, newUserDto.getId());
        }, "There is no item with this id");

        assertThrows(ObjectNotFoundException.class, () -> {
            itemController.findItemById(1, newUserDto.getId());
        }, "There is no item with this id");

    }

    @DirtiesContext
    @Test
    public void search_returnsTheCorrectList_underNormalConditions() {

        UserDto newUserDto = userController.createUser(userDto);
        ItemDto itemDto1 = itemController.createItem(itemDto, newUserDto.getId());
        ItemDto itemDto2 = itemController.createItem(
                ItemDto.builder().name("дрель V100500").description("Непростая дрель").available(true).build(),
                newUserDto.getId());

        List<ItemDto> itemDtoList1 = itemController.searchItems("дрель", 0, 10);
        List<ItemDto> itemDtoList2 = itemController.searchItems("дрель", 1, 1);

        assertEquals(itemDtoList1.size(), 2);
        assertEquals(itemDtoList1.get(0).getId(), itemDto1.getId());
        assertEquals(itemDtoList1.get(0).getName(), itemDto1.getName());
        assertEquals(itemDtoList1.get(0).getDescription(), itemDto1.getDescription());
        assertEquals(itemDtoList1.get(0).getAvailable(), itemDto1.getAvailable());
        assertEquals(itemDtoList1.get(0).getOwner().getId(), newUserDto.getId());
        assertEquals(itemDtoList2.size(), 1);
        assertEquals(itemDtoList2.get(0).getId(), itemDto2.getId());
        assertEquals(itemDtoList2.get(0).getName(), itemDto2.getName());
        assertEquals(itemDtoList2.get(0).getDescription(), itemDto2.getDescription());
        assertEquals(itemDtoList2.get(0).getAvailable(), itemDto2.getAvailable());
        assertEquals(itemDtoList2.get(0).getOwner().getId(), newUserDto.getId());
    }
}
