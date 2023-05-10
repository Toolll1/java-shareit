package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

        return itemService.findById(itemId);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto dto, @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemService.create(dto, userId);
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
