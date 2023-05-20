package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

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
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer itemId;
  //  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Item item;
  //  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User booker;
    private BookingStatus status;
}
