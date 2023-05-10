package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;

@Service
public class ItemMapper {

    public ItemDto objectToDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .ownerId(item.getOwnerId())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(item.getRequestId())
                .available(item.getAvailable())
                .build();
    }

    public Item dtoToObject(ItemDto dto, Integer userId) {

        return Item.builder()
                .id(dto.getId())
                .ownerId(userId)
                .name(dto.getName())
                .description(dto.getDescription())
                .requestId(dto.getRequestId())
                .available(dto.getAvailable())
                .build();
    }
}
