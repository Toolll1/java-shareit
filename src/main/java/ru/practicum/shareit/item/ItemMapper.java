package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;

@Service
public class ItemMapper {

    public ItemDto itemToDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .request(item.getRequest())
                .available(item.getAvailable())
                .build();
    }

    public Item dtoToItem(ItemDto dto, Integer userId) {

        return Item.builder()
                .id(dto.getId())
                .owner(userId)
                .name(dto.getName())
                .description(dto.getDescription())
                .request(dto.getRequest())
                .available(dto.getAvailable())
                .build();
    }
}
