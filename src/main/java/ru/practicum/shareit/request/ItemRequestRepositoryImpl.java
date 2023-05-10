package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemRequestRepositoryImpl implements ItemRequestRepository {
    @Override
    public List<ItemRequest> findAll() {

        throw new UnsupportedOperationException("method in development");
    }

    @Override
    public ItemRequest findById(int itemRequestId) {

        throw new UnsupportedOperationException("method in development");
    }

    @Override
    public ItemRequest create(ItemRequest itemRequest) {

        throw new UnsupportedOperationException("method in development");
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {

        throw new UnsupportedOperationException("method in development");
    }

    @Override
    public void deleteItemRequest(int itemRequestId) {

        throw new UnsupportedOperationException("method in development");
    }
}
