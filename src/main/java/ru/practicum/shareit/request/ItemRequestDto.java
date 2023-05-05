package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.Past;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ItemRequestDto {

    private Integer id;
    public String description;
    private Integer requestor;
    @Past
    private LocalDateTime created;
}
