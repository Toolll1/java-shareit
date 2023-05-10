package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;

@Service
public class ItemRequestMapper {

    public ItemRequestDto objectToDto(ItemRequest itemRequest) {

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestorId())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest dtoToObject(ItemRequestDto dto) {

        return ItemRequest.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestorId(dto.getRequestorId())
                .created(dto.getCreated())
                .build();
    }
}
