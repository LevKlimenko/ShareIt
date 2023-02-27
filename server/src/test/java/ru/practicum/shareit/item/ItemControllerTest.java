package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentIncomingDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    ItemInDto itemDto;
    Item item;

    @BeforeEach
    void beforeEach() {
        item = new Item(1L, "item", "itemDesc", true,
                new User(), null, null, null, null);
        itemDto = ItemInDto.builder()
                .name("item")
                .description("itemDesc")
                .available(true)
                .build();
    }

    @SneakyThrows
    @Test
    void createItemOk() {
        when(itemService.save(anyLong(), any())).thenReturn(ItemMapper.toItemDto(item));
        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty())
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.requestId").isEmpty());
        verify(itemService).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateItemWithOkStatus() {
        Item updatedItem = new Item(1L, "updateItem", "updateDescr", false, new User(),
                null, new Booking(), new Booking(), null);
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(ItemMapper.toItemDto(updatedItem));
        mvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedItem.getId()))
                .andExpect(jsonPath("$.description").value(updatedItem.getDescription()))
                .andExpect(jsonPath("$.available").value(updatedItem.getAvailable()))
                .andExpect(jsonPath("$.name").value(updatedItem.getName()))
                .andExpect(jsonPath("$.requestId").isEmpty())
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty())
                .andExpect(jsonPath("$.comments").isEmpty());

        verify(itemService).update(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateItemWithIncorrectId() {
        when(itemService.update(anyLong(), anyLong(), any())).thenThrow(NotFoundException.class);
        mvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService).update(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getByCorrectId() {
        when(itemService.findById(anyLong(), anyLong())).thenReturn(ItemMapper.toItemDto(item));
        mvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.requestId").isEmpty())
                .andExpect(jsonPath("$.lastBooking").isEmpty())
                .andExpect(jsonPath("$.nextBooking").isEmpty())
                .andExpect(jsonPath("$.comments").isEmpty());

        verify(itemService).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getByIncorrectId() {
        when(itemService.findById(anyLong(), anyLong())).thenThrow(NotFoundException.class);
        mvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllByOwnerWithBadPage() {
        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findByUserId(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllByOwnerWithBadSize() {
        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findByUserId(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void searchWithBadPage() {
        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "item")
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findByString(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void searchWithBadSize() {
        mvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "item")
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findByString(anyString(), anyInt(), anyInt());
    }


    @SneakyThrows
    @Test
    void addComment() {
        CommentIncomingDto commentIncomingDto = new CommentIncomingDto("comment");
        Comment comment = new Comment(1L, "comment", item, User.builder().name("userName").build(), LocalDateTime.now().plusMinutes(1));
        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(CommentMapper.toCommentDto(comment));
        mvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentIncomingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.authorName").value(comment.getAuthor().getName()))
                .andExpect(jsonPath("$.created").isNotEmpty());

        verify(itemService).createComment(anyLong(), anyLong(), any());
    }
}