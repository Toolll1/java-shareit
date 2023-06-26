package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    private final int itemId = 1;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final CommentDto commentDto = CommentDto.builder()
            .id(1)
            .text("text")
            .itemId(1)
            .authorId(2)
            .authorName("authorName")
            .created(LocalDateTime.now())
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(itemId)
            .name("дрель")
            .description("Простая дрель")
            .description("Простая дрель")
            .owner(User.builder().id(1).name("user1").email("user1@user.com").build())
            .requestId(1)
            .comments(List.of(commentDto))
            .available(true)
            .build();

    @Test
    void createComment() throws Exception {

        when(itemService.createComment(any(), anyInt(), anyInt())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(commentDto.getId()))
                .andExpect(jsonPath("itemId").value(commentDto.getItemId()))
                .andExpect(jsonPath("text").value(commentDto.getText()))
                .andExpect(jsonPath("authorName").value(commentDto.getAuthorName()));

        verify(itemService, times(1)).createComment(any(), anyInt(), anyInt());
    }

    @Test
    void findAllItemsByOwnerId() throws Exception {

        when(itemService.findAllByOwnerId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items?from=0&size=10")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$[0].requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));

        verify(itemService, times(1)).findAllByOwnerId(anyInt(), anyInt(), anyInt());
    }

    @Test
    void searchItems() throws Exception {

        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?from=0&size=10").param("text", "text")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$[0].requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));

        verify(itemService, times(1)).searchItems(anyString(), anyInt(), anyInt());
    }

    @Test
    void findItemById() throws Exception {

        when(itemService.findItemById(anyInt(), anyInt())).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(itemDto.getId()))
                .andExpect(jsonPath("available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("comments", hasSize(1)))
                .andExpect(jsonPath("name").value(itemDto.getName()));

        verify(itemService, times(1)).findItemById(anyInt(), anyInt());
    }

    @Test
    void deleteItem() throws Exception {

        mvc.perform(delete("/items/{itemId}", itemId).header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(anyInt(), anyInt());
    }

    @Test
    void createRequest() throws Exception {

        when(itemService.createItem(any(), anyInt())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(itemDto.getId()))
                .andExpect(jsonPath("available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("comments", hasSize(1)))
                .andExpect(jsonPath("name").value(itemDto.getName()));

        verify(itemService, times(1)).createItem(any(), anyInt());
    }

    @Test
    void updateItem() throws Exception {

        when(itemService.updateItem(any(), anyInt())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(itemDto.getId()))
                .andExpect(jsonPath("available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("requestId").value(itemDto.getRequestId()))
                .andExpect(jsonPath("comments", hasSize(1)))
                .andExpect(jsonPath("name").value(itemDto.getName()));

        verify(itemService, times(1)).updateItem(any(), anyInt());
    }
}
