package ru.practicum.shareit.user;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserDto {

    private Integer id;
    private String name;
    private String email;
}
