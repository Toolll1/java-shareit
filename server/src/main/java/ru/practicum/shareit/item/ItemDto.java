package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingDtoMini;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Integer requestId;
    private BookingDtoMini lastBooking;
    private BookingDtoMini nextBooking;
    private List<CommentDto> comments;
}
