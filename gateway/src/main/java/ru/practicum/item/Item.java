package ru.practicum.item;

import ru.practicum.request.ItemRequest;
import ru.practicum.user.User;

public class Item {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
