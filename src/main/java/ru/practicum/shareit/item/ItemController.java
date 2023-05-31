package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                               @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return itemService.findAllByOwnerId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable int itemId,
                                @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.findById(itemId, userId);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto dto,
                          @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.create(dto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto dto,
                                    @RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @PathVariable int itemId) {

        dto.setCreated(LocalDateTime.now());

        return itemService.createComment(dto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestBody ItemDto dto,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {

        dto.setId(itemId);

        return itemService.updateItem(dto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId,
                           @RequestHeader("X-Sharer-User-Id") Integer userId) {

        itemService.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                     @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return itemService.search(text, from, size);
    }
}
