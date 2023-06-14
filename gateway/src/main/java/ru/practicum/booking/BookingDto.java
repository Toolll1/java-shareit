package ru.practicum.booking;

import lombok.*;
import ru.practicum.item.Item;
import ru.practicum.user.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class BookingDto {

    private Integer id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private Integer itemId;
    private Item item;
    private UserDto booker;
    private BookingStatus status;
}
