package ru.practicum.shareit.item;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Item {

    private Integer id;
    public String name;
    public String description;
    public Boolean available;
    public Integer ownerId;
    public Integer requestId;
}
