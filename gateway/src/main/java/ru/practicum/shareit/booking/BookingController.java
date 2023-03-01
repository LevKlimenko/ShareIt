package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody BookingIncomingDto bookingInDto) {
        return bookingClient.saveBooking(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL")
                                                          String stateBooking,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "20") @Positive int size) {
        BookingState state = BookingState.from(stateBooking);
        log.info("Get bookings by booker with state {}, bookerId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "state", defaultValue = "ALL")
                                                         String stateBooking,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "20") @Positive int size) {
        BookingState state = BookingState.from(stateBooking);
        log.info("Get bookings by booker with state {}, bookerId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getAllByOwner(userId, state, from, size);
    }
}