package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<Item> findAllByOwnerId(Integer userId) {

        log.info("I received a request to search for all the items added by the user to the id " + userId);

        return itemRepository.findAllByOwnerId(userId);
    }

    public Item findById(int itemId) {

        log.info("Searching for a item with an id " + itemId);

        return itemRepository.findById(itemId);
    }

    public Item create(Item item) {

        Item newItem = itemRepository.create(item);
        log.info("I received a request to create a item " + newItem);

        return newItem;
    }

    public Item change(Item item) {

        Item newItem = itemRepository.change(item);
        log.info("I received a request to update a item\n" + "Old item " + item +  "\nNew item " + newItem);

        return newItem;
    }

    public void deleteItem(int itemId) {

        itemRepository.deleteItem(itemId);
        log.info("I received a request to delete a item with an id " + itemId);
    }

    public List<Item> search(String text) {

        log.info("I received a request to search for things whose name or description contains text: " + text);

        return itemRepository.search(text);
    }
}
