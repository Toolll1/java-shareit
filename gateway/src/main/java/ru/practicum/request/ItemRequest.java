package ru.practicum.request;

import ru.practicum.user.UserDto;

import java.time.LocalDateTime;

public class ItemRequest {

    private Integer id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;

}
