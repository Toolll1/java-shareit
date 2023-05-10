package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequest> findAll() {

        return itemRequestService.findAll();
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequest findById(@PathVariable int itemRequestId) {

        return itemRequestService.findById(itemRequestId);
    }

    @PostMapping
    public ItemRequest create(@Valid @RequestBody ItemRequest itemRequest) {

        return itemRequestService.create(itemRequest);
    }

    @PutMapping
    public ItemRequest update(@Valid @RequestBody ItemRequest itemRequest) {

        return itemRequestService.update(itemRequest);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteItemRequest(@PathVariable int itemRequestId) {

        itemRequestService.deleteItemRequest(itemRequestId);
    }
}
