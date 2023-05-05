package ru.practicum.shareit.request;

import java.util.List;

interface ItemRequestRepository {
    List<ItemRequest> findAll();

    ItemRequest findById(int itemRequestId);

    ItemRequest create(ItemRequest itemRequest);

    ItemRequest put(ItemRequest itemRequest);

    void deleteItemRequest(int itemRequestId);
}
