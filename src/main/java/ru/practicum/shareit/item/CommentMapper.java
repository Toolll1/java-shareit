package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
public class CommentMapper {

    private final UserService userService;
    private final UserMapper userMapper;

    public static CommentDto objectToDto(Comment comment) {

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment dtoToObject(CommentDto dto, Integer userId, Item item) {

        return Comment.builder()
                .id(dto.getId())
                .text(dto.getText())
                .item(item)
                .author(userMapper.dtoToObject(userService.findById(userId)))
                .created(dto.getCreated())
                .build();
    }
}
