package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @GetMapping
    public List<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer userId) {

        List<ItemDto> dtoList = new ArrayList<>();

        List<Item> itemsList = itemService.findAllByOwnerId(userId);

        for (Item item : itemsList) {
            dtoList.add(itemMapper.itemToDto(item));
        }

        return dtoList;
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return itemMapper.itemToDto(itemService.findById(itemId));
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto dto, @RequestHeader("X-Sharer-User-Id") Integer userId) {

        if (userService.findById(userId) == null) {
            throw new ObjectNotFoundException("There is no user with this id");
        }

        Item item = itemMapper.dtoToItem(dto, userId);

        return itemMapper.itemToDto(itemService.create(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto change(@PathVariable int itemId, @RequestBody ItemDto dto,
                          @RequestHeader("X-Sharer-User-Id") Integer userId) {

        dto.setId(itemId);

        if (userService.findById(userId) == null) {
            throw new ObjectNotFoundException("There is no user with this id");
        }

        Item item = itemMapper.dtoToItem(dto, userId);

        return itemMapper.itemToDto(itemService.change(item));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {

        if (itemService.findById(itemId).getOwner().equals(userId)) {
            itemService.deleteItem(itemId);
        } else {
            throw new ValidateException("Only its owner can delete an item");
        }
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {

        List<ItemDto> dtoList = new ArrayList<>();
        List<Item> itemsList = itemService.search(text);

        for (Item item : itemsList) {
            dtoList.add(itemMapper.itemToDto(item));
        }

        return dtoList;
    }
}
