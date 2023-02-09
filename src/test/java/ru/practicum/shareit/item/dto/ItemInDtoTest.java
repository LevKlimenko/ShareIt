package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemInDtoTest {
    @Autowired
    private JacksonTester<ItemInDto> json;


    @SneakyThrows
    @Test
    void testItemInDto(){
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@email.ru")
                .build();
        ItemRequest itemRequest = new ItemRequest(1L,"requestDescr",user, LocalDateTime.now());
        ItemInDto item = ItemInDto.builder()
                .id(1L)
                .name("item")
                .description("itemDesc")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        JsonContent<ItemInDto> result = json.write(item);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }


}
