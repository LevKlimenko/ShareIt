package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.validation.BookingPeriodValidation;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@BookingPeriodValidation(message = "End booking must be after start booking")
public class BookingIncomingDto {
    @NotNull
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    @FutureOrPresent
    private LocalDateTime end;
}