package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;


/**
 *     private Integer id;
 *     public String description;
 *     private Integer requestor;
 *     @Past
 *     private LocalDateTime created;
 */
@Service
public class ItemRequestMapper {

    public ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest dtoToItemRequest(ItemRequestDto dto) {

        return ItemRequest.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestor(dto.getRequestor())
                .created(dto.getCreated())
                .build();
    }
}
