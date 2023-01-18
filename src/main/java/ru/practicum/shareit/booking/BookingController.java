package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.enumBooking.State;
import ru.practicum.shareit.exceptions.InvalidStateException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping()
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingIncomingDto bookingInDto) {
        return service.save(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                              @RequestParam("approved") Boolean approved) {
        return service.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return service.findById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(required = false, defaultValue = "ALL") String state) {
        return service.findAllForBooker(userId, parseBookingState(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        return service.findAllForOwner(userId, parseBookingState(state));
    }

    private State parseBookingState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new InvalidStateException("Unknown state: " + state);
        }
    }
}