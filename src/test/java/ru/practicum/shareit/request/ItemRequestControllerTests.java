package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemDtoMini;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    private final int requestId = 1;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(requestId)
            .description("Хотел бы воспользоваться щёткой для обуви")
            .created(LocalDateTime.now())
            .requestor(User.builder().id(1).name("user1").email("user1@user.com").build())
            .items(List.of(ItemDtoMini.builder().id(1).name("дрель").description("Простая дрель").available(true).requestId(1).ownerId(2).build()))
            .build();

    @Test
    void findAllRequest() throws Exception {

        when(itemRequestService.findAllRequest(anyInt())).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].requestor").value(itemRequestDto.getRequestor()));

        verify(itemRequestService, Mockito.times(1)).findAllRequest(anyInt());
    }

    @Test
    void findAllRequest2() throws Exception {

        when(itemRequestService.findAllRequest(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].requestor").value(itemRequestDto.getRequestor()));

        verify(itemRequestService, Mockito.times(1)).findAllRequest(anyInt(), anyInt(), anyInt());
    }

    @Test
    void findRequestById() throws Exception {

        when(itemRequestService.findRequestById(anyInt(), anyInt())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{itemRequestId}", requestId)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("items", hasSize(1)))
                .andExpect(jsonPath("requestor").value(itemRequestDto.getRequestor()));

        verify(itemRequestService, times(1)).findRequestById(anyInt(), anyInt());
    }

    @Test
    void deleteRequest() throws Exception {

        mvc.perform(delete("/requests/{itemRequestId}", requestId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemRequestService, Mockito.times(1)).deleteRequest(requestId);
    }

    @Test
    void updateRequest() throws Exception {

        when(itemRequestService.updateRequest(any(), anyInt())).thenReturn(itemRequestDto);

        mvc.perform(put("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("items", hasSize(1)))
                .andExpect(jsonPath("requestor").value(itemRequestDto.getRequestor()));

        verify(itemRequestService, times(1)).updateRequest(any(), anyInt());
    }

    @Test
    void createRequest() throws Exception {

        when(itemRequestService.createRequest(any(), anyInt())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("items", hasSize(1)))
                .andExpect(jsonPath("requestor").value(itemRequestDto.getRequestor()));

        verify(itemRequestService, times(1)).createRequest(any(), anyInt());
    }
}
