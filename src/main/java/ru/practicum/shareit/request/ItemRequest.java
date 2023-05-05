package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.Past;
import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор запроса;
 * description — текст запроса, содержащий описание требуемой вещи;
 * requestor — пользователь, создавший запрос;
 * created — дата и время создания запроса.
 */

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
    private Integer requestor;
    @Past
    private LocalDateTime created;
}
