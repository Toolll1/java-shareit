package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    public List<ItemDto> findAllByOwnerId(Integer userId) {

        log.info("I received a request to search for all the items added by the user to the id " + userId);

        List<Item> itemsList = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> dtoList = new ArrayList<>();

        for (Item item : itemsList) {
            dtoList.add(itemMapper.objectToDto(item));
        }

        return dtoList;
    }

    public ItemDto findById(int itemId) {

        log.info("Searching for a item with an id " + itemId);

        return itemMapper.objectToDto(itemRepository.findById(itemId));
    }

    public ItemDto create(ItemDto dto, Integer userId) {

        if (userService.findById(userId) == null) {
            throw new ObjectNotFoundException("There is no user with this id");
        }

        Item item = itemMapper.dtoToObject(dto, userId);
        ItemDto itemDto = itemMapper.objectToDto(itemRepository.create(item));

        log.info("I received a request to create a item " + itemDto);

        return itemDto;
    }

    public ItemDto update(ItemDto dto, Integer userId) {

        if (userService.findById(userId) == null) {
            throw new ObjectNotFoundException("There is no user with this id");
        }

        Item item = itemMapper.dtoToObject(dto, userId);
        ItemDto itemDto = itemMapper.objectToDto(itemRepository.update(item));

        log.info("I received a request to update a item\n" + "Old item " + item + "\nNew item " + itemDto);

        return itemDto;
    }

    public void deleteItem(int itemId, Integer userId) {

        if (findById(itemId).getOwnerId().equals(userId)) {
            itemRepository.deleteItem(itemId);
        } else {
            throw new ValidateException("Only its owner can delete an item");
        }

        log.info("I received a request to delete a item with an id " + itemId);
    }

    public List<ItemDto> search(String text) {

        log.info("I received a request to search for things whose name or description contains text: " + text);

        List<ItemDto> dtoList = new ArrayList<>();
        List<Item> itemsList = itemRepository.search(text);

        for (Item item : itemsList) {
            dtoList.add(itemMapper.objectToDto(item));
        }
        dtoList.sort(Comparator.comparing(ItemDto::getId));

        return dtoList;
    }
}
