package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.shareit.booking.BookingDtoMini;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequest;
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;
    private ItemRequest request;
    private BookingDtoMini lastBooking;
    private BookingDtoMini nextBooking;
    private List<CommentDto> comments;
}
