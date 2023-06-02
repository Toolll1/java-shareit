package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTests {

    private final UserController userController;
    private final ItemRequestController requestController;
    private final ItemController itemController;

    UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
    ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Хотел бы воспользоваться щёткой для обуви").build();

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectItemRequestDto_underNormalConditions() {

        UserDto user = userController.createUser(userDto);
        ItemRequestDto request = requestController.createRequest(itemRequestDto, user.getId());
        ItemDto item = itemController.createItem(ItemDto.builder().name("дрель").requestId(request.getId())
                .description("Простая дрель").available(true).build(), user.getId());
        ItemRequestDto request1 = requestController.findRequestById(user.getId(), request.getId());

        assertEquals(request.getId(), 1);
        assertEquals(request.getDescription(), "Хотел бы воспользоваться щёткой для обуви");
        assertEquals(request.getRequestor().getId(), user.getId());
        assertNull(request.getItems());
        assertEquals(request1.getItems().size(), 1);
        assertEquals(request1.getItems().get(0).getId(), item.getId());
    }

    @DirtiesContext
    @Test
    public void update_returnsTheCorrectItemRequestDto_underNormalConditions() {

        UserDto user = userController.createUser(userDto);
        requestController.createRequest(itemRequestDto, user.getId());
        ItemRequestDto request1 = requestController.updateRequest(
                ItemRequestDto.builder().description("Хотел бы воспользоваться щёткой для зубов").id(1).build(), user.getId());

        assertEquals(request1.getId(), 1);
        assertEquals(request1.getDescription(), "Хотел бы воспользоваться щёткой для зубов");
        assertEquals(request1.getRequestor().getId(), user.getId());
        assertNull(request1.getItems());
    }

    @DirtiesContext
    @Test
    public void find_returnsTheCorrectItemRequestDto_underNormalConditions() {

        UserDto user = userController.createUser(userDto);
        ItemRequestDto request = requestController.createRequest(itemRequestDto, user.getId());
        ItemDto item = itemController.createItem(ItemDto.builder().name("дрель").requestId(request.getId())
                .description("Простая дрель").available(true).build(), user.getId());
        ItemRequestDto request1 = requestController.findRequestById(user.getId(), request.getId());

        assertEquals(request1.getId(), 1);
        assertEquals(request1.getItems().size(), 1);
        assertEquals(request1.getDescription(), "Хотел бы воспользоваться щёткой для обуви");
        assertEquals(request1.getRequestor().getId(), user.getId());
        assertEquals(request1.getItems().get(0).getId(), item.getId());
    }

    @DirtiesContext
    @Test
    public void findAll_returnsTheCorrectItemRequestDto_underNormalConditions() {

        UserDto user1 = userController.createUser(userDto);
        ItemRequestDto request = requestController.createRequest(itemRequestDto, user1.getId());
        ItemDto item = itemController.createItem(ItemDto.builder().name("дрель").requestId(request.getId())
                .description("Простая дрель").available(true).build(), user1.getId());

        List<ItemRequestDto> requests1 = requestController.findAllRequest(user1.getId());

        assertEquals(requests1.size(), 1);
        assertEquals(requests1.get(0).getId(), 1);
        assertEquals(requests1.get(0).getItems().size(), 1);
        assertEquals(requests1.get(0).getDescription(), "Хотел бы воспользоваться щёткой для обуви");
        assertEquals(requests1.get(0).getRequestor().getId(), user1.getId());
        assertEquals(requests1.get(0).getItems().get(0).getId(), item.getId());

        UserDto user2 = userController.createUser(UserDto.builder().name("user1").email("user1@user.com").build());
        List<ItemRequestDto> requests2 = requestController.findAllRequest(user2.getId(), 0, 10);

        assertEquals(requests2.size(), 1);
        assertEquals(requests2.get(0).getId(), 1);
        assertEquals(requests2.get(0).getItems().size(), 1);
        assertEquals(requests2.get(0).getDescription(), "Хотел бы воспользоваться щёткой для обуви");
        assertEquals(requests2.get(0).getRequestor().getId(), user1.getId());
        assertEquals(requests2.get(0).getItems().get(0).getId(), item.getId());

        assertThrows(BadRequestException.class, () -> requestController.findAllRequest(user1.getId(), -1, 10));
        assertThrows(BadRequestException.class, () -> requestController.findAllRequest(user1.getId(), 0, 0));
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        UserDto user = userController.createUser(userDto);
        ItemRequestDto request = requestController.createRequest(itemRequestDto, user.getId());

        requestController.deleteRequest(request.getId());

        assertThrows(ObjectNotFoundException.class, () -> requestController.findRequestById(user.getId(), request.getId()));
        assertEquals(requestController.findAllRequest(user.getId()).size(), 0);
        assertEquals(requestController.findAllRequest(user.getId(), 0, 10).size(), 0);
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        UserDto user = userController.createUser(userDto);

        assertThrows(EmptyResultDataAccessException.class, () -> requestController.deleteRequest(999), "There is no object with this idy");
        assertEquals(requestController.findAllRequest(user.getId()).size(), 0);
        assertEquals(requestController.findAllRequest(user.getId(), 0, 10).size(), 0);
    }
}
