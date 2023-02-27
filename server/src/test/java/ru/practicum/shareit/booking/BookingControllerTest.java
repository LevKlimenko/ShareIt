package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enumBooking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    BookingIncomingDto bookingIncomingDto;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        bookingIncomingDto = BookingIncomingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(3)).build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(3))
                .item(Item.builder().id(1L).name("item").owner(User.builder().id(2L).build()).build())
                .booker(User.builder().id(1L).name("booker").build())
                .status(Status.WAITING)
                .build();
    }

    @SneakyThrows
    @Test
    void createWithBookingOk() {
        when(bookingService.save(anyLong(), any())).thenReturn(BookingMapper.toBookingDto(booking));
        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingIncomingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(booking.getItem().getName()));

        verify(bookingService).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void createWithTimeCrossing() {
        bookingIncomingDto.setEnd(LocalDateTime.now().plusMinutes(10));
        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingIncomingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void approveRequestWithOk() {
        booking.setStatus(Status.APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), any())).thenReturn(BookingMapper.toBookingDto(booking));

        mvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(booking.getItem().getName()));

        verify(bookingService).approve(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void findByIdWithOk() {
        when(bookingService.findById(anyLong(), anyLong())).thenReturn(BookingMapper.toBookingDto(booking));
        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(booking.getItem().getName()));


        verify(bookingService).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findByIdWithBadId() {
        when(bookingService.findById(anyLong(), anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findAllByUserWithOk() {
        when(bookingService.findAllForBooker(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingMapper.toBookingDto(booking)));

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(booking.getId()))
                .andExpect(jsonPath("$.[0].start").isNotEmpty())
                .andExpect(jsonPath("$.[0].end").isNotEmpty())
                .andExpect(jsonPath("$.[0].status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.[0].booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.[0].booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.[0].item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.[0].item.name").value(booking.getItem().getName()));

        verify(bookingService).findAllForBooker(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByUserWithLowCaseState() {
        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "waiting")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findAllByUserWithoutBooking() {
        when(bookingService.findAllForBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());

        verify(bookingService).findAllForBooker(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByUserAndBadFrom() {
        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findAllForBooker(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByUserAndBadSize() {
        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findAllForBooker(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByOwnerWithOk() {
        when(bookingService.findAllForOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(BookingMapper.toBookingDto(booking)));

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(booking.getId()))
                .andExpect(jsonPath("$.[0].start").isNotEmpty())
                .andExpect(jsonPath("$.[0].end").isNotEmpty())
                .andExpect(jsonPath("$.[0].status").value(booking.getStatus().toString()))
                .andExpect(jsonPath("$.[0].booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.[0].booker.name").value(booking.getBooker().getName()))
                .andExpect(jsonPath("$.[0].item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.[0].item.name").value(booking.getItem().getName()));

        verify(bookingService).findAllForOwner(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByOwnerWithLowCaseState() {
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "waiting")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findAllByOwnerAndBadFrom() {
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findAllForOwner(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByOwnerAndBadSize() {
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findAllForOwner(anyLong(), any(), anyInt(), anyInt());
    }
}