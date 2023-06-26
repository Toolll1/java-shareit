package ru.practicum.request;

import lombok.*;
import ru.practicum.item.ItemDtoMini;
import ru.practicum.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
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
    @NotEmpty
    private String description;
    private User requestor;
    @Past
    private LocalDateTime created;
    private List<ItemDtoMini> items;
}
