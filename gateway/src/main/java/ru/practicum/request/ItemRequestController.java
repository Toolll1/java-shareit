package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findAllRequest(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestClient.findAllRequest(userId, null);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequest(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                 @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return itemRequestClient.findAllRequest(userId, from, size, null);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                  @PathVariable int itemRequestId) {

        return itemRequestClient.findRequestById(itemRequestId, userId, null);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestClient.createRequest(itemRequestDto, userId);
    }

    @PutMapping
    public ResponseEntity<Object> updateRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {

        return itemRequestClient.updateRequest(itemRequestDto, userId);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteRequest(@PathVariable int itemRequestId) {

        itemRequestClient.deleteRequest(itemRequestId);
    }
}
