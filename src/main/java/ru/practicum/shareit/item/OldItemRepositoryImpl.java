package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.ObjectNotFoundException;

import java.util.*;

public class OldItemRepositoryImpl implements OldItemRepository{

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer itemId = 1;

    @Override
    public List<Item> findAllByOwnerId(Integer userId) {

        List<Item> itemsByOwnerId = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                itemsByOwnerId.add(item);
            }
        }
        return itemsByOwnerId;
    }

    @Override
    public Item findById(int itemId) {

        return items.get(itemId);
    }

    @Override
    public Item create(Item item) {

        Integer id = itemId++;
        item.setId(id);
        items.put(id, item);

        return item;
    }

    @Override
    public Item update(Item newItem) {

        Item item = items.get(newItem.getId());

        if (!newItem.getOwner().getId().equals(item.getOwner().getId())) {
            throw new ObjectNotFoundException("You can't change the owner");
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getRequest() != null) {
            item.getRequest().setId(newItem.getRequest().getId());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }

        items.put(item.getId(), item);

        return item;
    }

    @Override
    public void deleteItem(int itemId) {

        items.remove(itemId);
    }

    @Override
    public List<Item> search(String text) {

        Set<Item> itemsBySearch = new HashSet<>();

        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }

        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable().equals(true)) {
                itemsBySearch.add(item);
            }
        }

        return new ArrayList<>(itemsBySearch);
    }
}
