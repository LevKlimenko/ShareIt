package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;
    UserDto userDto = new UserDto(1L,"userDto","userDto@test.ru");

    @SneakyThrows
    @Test
    void testMapper(){
        JsonContent<UserDto> check = jacksonTester.write(userDto);
        assertThat(check).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(check).extractingJsonPathStringValue("$.name").isEqualTo("userDto");
        assertThat(check).extractingJsonPathStringValue("$.email").isEqualTo("userDto@test.ru");
    }
}
