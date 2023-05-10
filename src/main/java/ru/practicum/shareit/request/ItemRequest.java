package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ItemRequest {

    private Integer id;
    public String description;
    private Integer requestorId;
    @Past
    private LocalDateTime created;
}
