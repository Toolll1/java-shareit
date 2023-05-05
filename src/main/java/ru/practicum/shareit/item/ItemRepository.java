package ru.practicum.shareit.item;

import java.util.List;

interface ItemRepository {
    List<Item> findAllByOwnerId(Integer userId);

    Item findById(int itemId);

    Item create(Item item);

    Item change(Item item);

    void deleteItem(int itemId);

    List<Item> search(String text);
}
