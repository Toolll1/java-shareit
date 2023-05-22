package ru.practicum.shareit.item;

import java.util.List;

interface OldItemRepository {

    List<Item> findAllByOwnerId(Integer userId);

    Item findById(int itemId);

    Item create(Item item);

    Item update(Item item);

    void deleteItem(int itemId);

    List<Item> search(String text);
}
