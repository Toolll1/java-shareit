package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
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

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    private final int bookingId = 1;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = BookingDto.builder()
            .id(bookingId)
            .booker(User.builder().id(1).name("user").email("user@user.com").build())
            .status(BookingStatus.WAITING)
            .item(Item.builder().id(1).name("name").description("description").build())
            .itemId(1)
            .start(LocalDateTime.now().plusSeconds(1))
            .end(LocalDateTime.now().plusSeconds(3))
            .build();

    @Test
    void createBooking() throws Exception {

        when(bookingService.createBooking(any(), anyInt())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(bookingDto.getId()))
                .andExpect(jsonPath("status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("item").value(bookingDto.getItem()));

        verify(bookingService, times(1)).createBooking(any(), anyInt());
    }

    @Test
    void updateBooking() throws Exception {

        when(bookingService.updateBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId).param("approved", "true")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(bookingDto.getId()))
                .andExpect(jsonPath("status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("item").value(bookingDto.getItem()));

        verify(bookingService, times(1)).updateBooking(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void deleteBooking() throws Exception {

        mvc.perform(delete("/bookings/{bookingId}", bookingId).header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService, times(1)).deleteBooking(anyInt(), anyInt());
    }

    @Test
    void findBookingById() throws Exception {

        when(bookingService.findBookingById(anyInt(), anyInt())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(bookingDto.getId()))
                .andExpect(jsonPath("status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("item").value(bookingDto.getItem()));

        verify(bookingService, times(1)).findBookingById(anyInt(), anyInt());
    }

    @Test
    void getBookingsForUser() throws Exception {

        when(bookingService.getUsersBooking(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()));

        verify(bookingService, times(1)).getUsersBooking(anyInt(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getBookingsForUsersItems() throws Exception {

        when(bookingService.getBookingsForUsersItems(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()));

        verify(bookingService, times(1)).getBookingsForUsersItems(anyInt(), anyString(), anyInt(), anyInt());
    }
}
