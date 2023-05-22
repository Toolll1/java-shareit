package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public static UserDto objectToDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUserName())
                .build();
    }

    public User dtoToObject(UserDto dto) {

        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .userName(dto.getName())
                .build();
    }
}
