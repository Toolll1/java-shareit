package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemRequestRepositoryImpl implements ItemRequestRepository {
    @Override
    public List<ItemRequest> findAll() {
        return null;
    }

    @Override
    public ItemRequest findById(int itemRequestId) {
        return null;
    }

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        return null;
    }

    @Override
    public ItemRequest put(ItemRequest itemRequest) {
        return null;
    }

    @Override
    public void deleteItemRequest(int itemRequestId) {

    }
}
