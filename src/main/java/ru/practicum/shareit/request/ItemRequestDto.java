package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Past;
import java.time.LocalDateTime;

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
    @Past
    private LocalDateTime created;
}
