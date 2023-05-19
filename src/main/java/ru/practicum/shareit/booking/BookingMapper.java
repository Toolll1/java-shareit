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
    private final ItemRepository itemRepository;

    public static BookingDto objectToDto(Booking booking) {

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getBookingStatus())
                .build();
    }

    public Booking dtoToObject(BookingDto dto, Integer userId) {

        ItemDto itemDto = itemService.findById(dto.getItemId(), userId);

        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(itemMapper.dtoToObject(itemDto, itemDto.getOwner().getId()))
                .booker(userMapper.dtoToObject(userService.findById(userId)))
                .bookingStatus(dto.getStatus())
                .build();
    }

    public Booking dtoToObject(BookingDto dto) {

        Item item = itemRepository.findById(dto.getItemId()).get();

        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(dto.getBooker())
                .bookingStatus(dto.getStatus())
                .build();
    }

    public static BookingDtoMini objectToDtoMini(Booking booking) {

        return BookingDtoMini.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getBookingStatus())
                .build();
    }
}
