package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingInDto {
    Long itemId;
    LocalDateTime start;
    LocalDateTime end;


}
