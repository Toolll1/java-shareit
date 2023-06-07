package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
public class ItemMapper {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRequestRepository itemRequestRepository;

    public static ItemDto objectToDto(Item item) {

        if (item.getRequest() != null) {
            return ItemDto.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(item.getName())
                    .description(item.getDescription())
                    .requestId(item.getRequest().getId())
                    .available(item.getAvailable())
                    .build();
        } else {
            return ItemDto.builder()
                    .id(item.getId())
                    .owner(item.getOwner())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .build();
        }

    }

    public Item dtoToObject(ItemDto dto, Integer userId) {

        if (dto.getRequestId() != null) {
            return Item.builder()
                    .id(dto.getId())
                    .owner(userMapper.dtoToObject(userService.findUserById(userId)))
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .request(itemRequestRepository.findById(dto.getRequestId()).get())
                    .available(dto.getAvailable())
                    .build();
        } else {
            return Item.builder()
                    .id(dto.getId())
                    .owner(userMapper.dtoToObject(userService.findUserById(userId)))
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .available(dto.getAvailable())
                    .build();
        }

    }

    public static ItemDtoMini objectToDtoMini(Item item) {

        return ItemDtoMini.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }
}
