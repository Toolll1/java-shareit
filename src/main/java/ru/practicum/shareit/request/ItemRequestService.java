package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    public List<ItemRequest> findAll() {

        return itemRequestRepository.findAll();
    }

    public ItemRequest findById(int itemRequestId) {

        return itemRequestRepository.findById(itemRequestId);
    }

    public ItemRequest create(ItemRequest itemRequest) {

        return itemRequestRepository.create(itemRequest);
    }

    public ItemRequest put(ItemRequest itemRequest) {

        return itemRequestRepository.put(itemRequest);
    }

    public void deleteItemRequest(int itemRequestId) {

        itemRequestRepository.deleteItemRequest(itemRequestId);
    }
}
