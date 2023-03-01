package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingIncomingDtoTest {
    @Autowired
    private JacksonTester<BookingIncomingDto> jacksonTester;

    @SneakyThrows
    @Test
    void testBookingIncomingDto() {
        BookingIncomingDto bookingIncomingDto = new BookingIncomingDto(1L, LocalDateTime.now(), LocalDateTime.now());

        JsonContent<BookingIncomingDto> result = jacksonTester.write(bookingIncomingDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}
