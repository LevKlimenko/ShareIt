package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;

    ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse("requestDesc");
    ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("requestDesc")
            .created(LocalDateTime.now().plusHours(1))
            .items(null)
            .build();

    @SneakyThrows
    @Test
    void saveIsOk() {
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.items").value(itemRequestDto.getItems()));
    }

    @SneakyThrows
    @Test
    void findAllWithIncorrectFrom() {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllWithIncorrectSize() {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).findAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByOwnerWithIncorrectFrom() {
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllByOwnerWithIncorrectSize() {
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).findAll(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findByIdWithOk() {
        when(itemRequestService.get(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());

        verify(itemRequestService).get(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findByIdWithNotFound() {
        when(itemRequestService.get(anyLong(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

