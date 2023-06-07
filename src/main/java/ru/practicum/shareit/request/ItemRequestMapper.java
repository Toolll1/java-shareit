package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final UserService userService;
    private final UserMapper userMapper;

    public static ItemRequestDto objectToDto(ItemRequest itemRequest) {

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest dtoToObject(ItemRequestDto dto, Integer userId) {

        return ItemRequest.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestor(userMapper.dtoToObject(userService.findUserById(userId)))
                .created(dto.getCreated())
                .build();
    }
}
