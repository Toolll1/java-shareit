package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapperTests {

    private final BookingMapper bookingMapper;

    @MockBean
    ItemRepository itemRepository;
    @MockBean
    UserService userService;

    private final User owner = User.builder().id(1).name("user").email("user@user.com").build();
    private final User booker = User.builder().id(2).name("user1").email("user1@user.com").build();
    private final Item item = Item.builder().id(1).name("дрель").description("Простая дрель").owner(owner).available(true).build();
    private final BookingDto bookingDto = BookingDto.builder().id(1).itemId(1).item(item).start(LocalDateTime.now().plusSeconds(1))
            .end(LocalDateTime.now().plusSeconds(30)).booker(booker).status(BookingStatus.WAITING).build();
    private final Booking booking = Booking.builder().id(1).item(item).start(LocalDateTime.now().plusSeconds(1))
            .end(LocalDateTime.now().plusSeconds(30)).booker(booker).status(BookingStatus.WAITING).build();

    @Test
    public void objectToDto() {

        BookingDto dto = BookingMapper.objectToDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(dto.getStatus(), booking.getStatus());
        assertEquals(dto.getItemId(), booking.getItem().getId());
        assertEquals(dto.getItem().getId(), booking.getItem().getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void dtoToObject() {

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userService.findById(anyInt())).thenReturn(UserDto.builder().id(2).name("user1").email("user1@user.com").build());

        Booking object1 = bookingMapper.dtoToObject(bookingDto);
        Booking object2 = bookingMapper.dtoToObject(bookingDto, owner.getId());

        assertEquals(bookingDto.getId(), object1.getId());
        assertEquals(bookingDto.getBooker().getId(), object1.getBooker().getId());
        assertEquals(bookingDto.getStatus(), object1.getStatus());
        assertEquals(bookingDto.getItemId(), object1.getItem().getId());
        assertEquals(bookingDto.getItem().getId(), object1.getItem().getId());
        assertEquals(bookingDto.getStart(), object1.getStart());
        assertEquals(bookingDto.getEnd(), object1.getEnd());
        assertEquals(bookingDto.getId(), object2.getId());
        assertEquals(bookingDto.getBooker().getId(), object2.getBooker().getId());
        assertEquals(bookingDto.getStatus(), object2.getStatus());
        assertEquals(bookingDto.getItemId(), object2.getItem().getId());
        assertEquals(bookingDto.getItem().getId(), object2.getItem().getId());
        assertEquals(bookingDto.getStart(), object2.getStart());
        assertEquals(bookingDto.getEnd(), object2.getEnd());
    }

    @Test
    public void objectToDtoMini() {

        BookingDtoMini dto = BookingMapper.objectToDtoMini(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStatus(), booking.getStatus());
        assertEquals(dto.getItemId(), booking.getItem().getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }
}
