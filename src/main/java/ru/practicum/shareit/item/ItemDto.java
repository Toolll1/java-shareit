package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingDtoMini;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private Integer requestId;
    private BookingDtoMini lastBooking;
    private BookingDtoMini nextBooking;
    private List<CommentDto> comments;
}
