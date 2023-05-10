package ru.practicum.shareit.item;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    public String name;
    @NotEmpty
    public String description;
    @NotNull
    public Boolean available;
    public Integer ownerId;
    public Integer requestId;
}
