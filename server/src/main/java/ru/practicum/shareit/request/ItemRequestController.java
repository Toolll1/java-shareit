package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> findAllRequest(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestService.findAllRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequest(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                               @RequestParam(value = "from") Integer from,
                                               @RequestParam(value = "size") Integer size) {

        return itemRequestService.findAllRequest(userId, from, size);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto findRequestById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                          @PathVariable int itemRequestId) {

        return itemRequestService.findRequestById(itemRequestId, userId);
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @PutMapping
    public ItemRequestDto updateRequest(@RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestService.updateRequest(itemRequestDto, userId);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteRequest(@PathVariable int itemRequestId) {

        itemRequestService.deleteRequest(itemRequestId);
    }
}
