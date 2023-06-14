package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
public class BookingMapper {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public static BookingDto objectToDto(Booking booking) {

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public Booking dtoToObject(BookingDto dto, Integer userId) {

        ItemDto itemDto = itemService.findItemById(dto.getItemId(), userId);

        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(itemMapper.dtoToObject(itemDto, itemDto.getOwner().getId()))
                .booker(userMapper.dtoToObject(userService.findUserById(userId)))
                .status(dto.getStatus())
                .build();
    }

    public Booking dtoToObject(BookingDto dto) {

        int userId = dto.getItem().getOwner().getId();
        Item item = itemMapper.dtoToObject(itemService.findItemById(dto.getItemId(), userId), userId);

        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(dto.getBooker())
                .status(dto.getStatus())
                .build();
    }

    public static BookingDtoMini objectToDtoMini(Booking booking) {

        return BookingDtoMini.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }
}
