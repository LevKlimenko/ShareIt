package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.enumBooking.State;

import java.util.List;

public interface BookingService {
    BookingDto save(Long userId, BookingIncomingDto bookingInDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllForOwner(Long userId, State state);

    List<BookingDto> findAllForBooker(Long userId, State state);
}