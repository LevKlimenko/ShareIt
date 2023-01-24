package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(Long userId, BookingIncomingDto bookingInDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllForOwner(Long userId, String state, int from, int size);

    List<BookingDto> findAllForBooker(Long userId, String state, int from, int size);
}