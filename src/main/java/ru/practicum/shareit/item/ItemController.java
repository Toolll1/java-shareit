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
    public List<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.findAllByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.findById(itemId, userId);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto dto, @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.create(dto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto dto, @RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int itemId) {

        dto.setCreated(LocalDateTime.now());

        return itemService.createComment(dto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable int itemId, @RequestBody ItemDto dto,
                          @RequestHeader("X-Sharer-User-Id") Integer userId) {

        dto.setId(itemId);

        return itemService.update(dto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {

        itemService.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {

        return itemService.search(text);
    }
}
