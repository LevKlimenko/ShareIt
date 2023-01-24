package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
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
                                    @RequestParam(required = false, defaultValue = "ALL") String state,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "20") @Positive int size) {
        return service.findAllForBooker(userId, state.toUpperCase(), from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String state,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "20") @Positive int size) {
        return service.findAllForOwner(userId, state.toUpperCase(), from, size);
    }


}