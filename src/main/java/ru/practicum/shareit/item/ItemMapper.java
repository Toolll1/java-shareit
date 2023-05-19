package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
public class ItemMapper {

    private final UserService userService;
    private final UserMapper userMapper;

    public static ItemDto objectToDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .request(item.getRequest())
                .available(item.getAvailable())
                .build();
    }

    public Item dtoToObject(ItemDto dto, Integer userId) {

        return Item.builder()
                .id(dto.getId())
                .owner(userMapper.dtoToObject(userService.findById(userId)))
                .name(dto.getName())
                .description(dto.getDescription())
                .request(dto.getRequest())
                .available(dto.getAvailable())
                .build();
    }
}
