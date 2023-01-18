package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentMapper;

import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        ItemDto itemDto = toItemDto(item);
        if (comments != null) {
            itemDto.setComments(comments
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        }

        return itemDto;
    }

    public static ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking,
                                    List<Comment> comments) {
        ItemDto itemDto = toItemDto(item, comments);
        if (lastBooking != null) {
            itemDto.setLastBooking(bookingToBookingBriefDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(bookingToBookingBriefDto(nextBooking));
        }
        return itemDto;
    }

    public static Item toItem(ItemInDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    private BookingBriefDto bookingToBookingBriefDto(Booking booking) {
        BookingBriefDto mapped = new BookingBriefDto();
        mapped.setId(booking.getId());
        mapped.setBookerId(booking.getBooker().getId());
        return mapped;
    }
}
