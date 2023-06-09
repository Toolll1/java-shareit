package ru.practicum.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserDto {

    private Integer id;
    @NotEmpty
    private String name;
    @Email
    @NotEmpty
    private String email;
}