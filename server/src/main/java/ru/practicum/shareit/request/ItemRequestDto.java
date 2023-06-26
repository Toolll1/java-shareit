package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.ItemDtoMini;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ItemRequestDto {

    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDtoMini> items;
}
