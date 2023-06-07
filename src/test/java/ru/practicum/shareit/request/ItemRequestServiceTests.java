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
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTests {

    private final UserService userService;
    private final ItemRequestService requestService;
    private final ItemService itemService;

    UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
    ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Хотел бы воспользоваться щёткой для обуви").build();

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectItemRequestDto_underNormalConditions() {

        UserDto user = userService.createUser(userDto);
        ItemRequestDto request = requestService.createRequest(itemRequestDto, user.getId());
        ItemDto item = itemService.createItem(ItemDto.builder().name("дрель").requestId(request.getId())
                .description("Простая дрель").available(true).build(), user.getId());
        ItemRequestDto request1 = requestService.findRequestById(user.getId(), request.getId());

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

        UserDto user = userService.createUser(userDto);
        requestService.createRequest(itemRequestDto, user.getId());
        ItemRequestDto request1 = requestService.updateRequest(
                ItemRequestDto.builder().description("Хотел бы воспользоваться щёткой для зубов").id(1).build(), user.getId());

        assertEquals(request1.getId(), 1);
        assertEquals(request1.getDescription(), "Хотел бы воспользоваться щёткой для зубов");
        assertEquals(request1.getRequestor().getId(), user.getId());
        assertNull(request1.getItems());
    }

    @DirtiesContext
    @Test
    public void find_returnsTheCorrectItemRequestDto_underNormalConditions() {

        UserDto user = userService.createUser(userDto);
        ItemRequestDto request = requestService.createRequest(itemRequestDto, user.getId());
        ItemDto item = itemService.createItem(ItemDto.builder().name("дрель").requestId(request.getId())
                .description("Простая дрель").available(true).build(), user.getId());
        ItemRequestDto request1 = requestService.findRequestById(user.getId(), request.getId());

        assertEquals(request1.getId(), 1);
        assertEquals(request1.getItems().size(), 1);
        assertEquals(request1.getDescription(), "Хотел бы воспользоваться щёткой для обуви");
        assertEquals(request1.getRequestor().getId(), user.getId());
        assertEquals(request1.getItems().get(0).getId(), item.getId());
    }

    @DirtiesContext
    @Test
    public void findAll_returnsTheCorrectItemRequestDto_underNormalConditions() {

        UserDto user1 = userService.createUser(userDto);
        ItemRequestDto request = requestService.createRequest(itemRequestDto, user1.getId());
        ItemDto item = itemService.createItem(ItemDto.builder().name("дрель").requestId(request.getId())
                .description("Простая дрель").available(true).build(), user1.getId());

        List<ItemRequestDto> requests1 = requestService.findAllRequest(user1.getId());

        assertEquals(requests1.size(), 1);
        assertEquals(requests1.get(0).getId(), 1);
        assertEquals(requests1.get(0).getItems().size(), 1);
        assertEquals(requests1.get(0).getDescription(), "Хотел бы воспользоваться щёткой для обуви");
        assertEquals(requests1.get(0).getRequestor().getId(), user1.getId());
        assertEquals(requests1.get(0).getItems().get(0).getId(), item.getId());

        UserDto user2 = userService.createUser(UserDto.builder().name("user1").email("user1@user.com").build());
        List<ItemRequestDto> requests2 = requestService.findAllRequest(user2.getId(), 0, 10);

        assertEquals(requests2.size(), 1);
        assertEquals(requests2.get(0).getId(), 1);
        assertEquals(requests2.get(0).getItems().size(), 1);
        assertEquals(requests2.get(0).getDescription(), "Хотел бы воспользоваться щёткой для обуви");
        assertEquals(requests2.get(0).getRequestor().getId(), user1.getId());
        assertEquals(requests2.get(0).getItems().get(0).getId(), item.getId());

        assertThrows(BadRequestException.class, () -> requestService.findAllRequest(user1.getId(), -1, 10));
        assertThrows(BadRequestException.class, () -> requestService.findAllRequest(user1.getId(), 0, 0));
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        UserDto user = userService.createUser(userDto);
        ItemRequestDto request = requestService.createRequest(itemRequestDto, user.getId());

        requestService.deleteRequest(request.getId());

        assertThrows(ObjectNotFoundException.class, () -> requestService.findRequestById(user.getId(), request.getId()));
        assertEquals(requestService.findAllRequest(user.getId()).size(), 0);
        assertEquals(requestService.findAllRequest(user.getId(), 0, 10).size(), 0);
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        UserDto user = userService.createUser(userDto);

        assertThrows(EmptyResultDataAccessException.class, () -> requestService.deleteRequest(999), "There is no object with this idy");
        assertEquals(requestService.findAllRequest(user.getId()).size(), 0);
        assertEquals(requestService.findAllRequest(user.getId(), 0, 10).size(), 0);
    }
}
