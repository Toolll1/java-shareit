package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> findAllRequest(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestService.findAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequest(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                               @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                               @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return itemRequestService.findAll(userId, from, size);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto findRequestById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                          @PathVariable int itemRequestId) {

        return itemRequestService.findById(itemRequestId, userId);
    }

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestService.create(itemRequestDto, userId);
    }

    @PutMapping
    public ItemRequestDto updateRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestService.update(itemRequestDto, userId);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteRequest(@PathVariable int itemRequestId) {

        itemRequestService.deleteItemRequest(itemRequestId);
    }
}
