package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.comment.CommentDto;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return itemClient.findAllByOwnerId(userId, from, size, null);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable int itemId,
                                               @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemClient.findItemById(itemId, userId, null);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto dto,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemClient.createItem(userId, dto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto dto,
                                                @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @PathVariable int itemId) {

        return itemClient.createComment(userId, itemId, dto);
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(@PathVariable int itemId,
                             @RequestBody ItemDto dto,
                             @RequestHeader("X-Sharer-User-Id") Integer userId) {


        return itemClient.updateItem(userId, dto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId,
                           @RequestHeader("X-Sharer-User-Id") Integer userId) {

        itemClient.deleteItem(itemId, userId, null);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                              @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemClient.searchItems(text, from, size, userId, null);
    }
}
