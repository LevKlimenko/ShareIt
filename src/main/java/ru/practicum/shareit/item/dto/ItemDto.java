package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingBriefDto lastBooking;
    private BookingBriefDto nextBooking;
    private List<CommentDto> comments;
}