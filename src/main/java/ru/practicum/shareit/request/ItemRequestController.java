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

        return itemRequestService.findAllRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequest(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                               @RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return itemRequestService.findAllRequest(userId, from, size);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto findRequestById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                          @PathVariable int itemRequestId) {

        return itemRequestService.findRequestById(itemRequestId, userId);
    }

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @PutMapping
    public ItemRequestDto updateRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestService.updateRequest(itemRequestDto, userId);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteRequest(@PathVariable int itemRequestId) {

        itemRequestService.deleteRequest(itemRequestId);
    }
}
