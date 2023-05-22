package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemInOrderByCreatedDesc(List<Item> items);
}
