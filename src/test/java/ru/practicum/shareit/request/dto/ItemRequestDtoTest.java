package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class RequestShortDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDtoResponse> json;

    @Test
    void testRequestShortDto() throws Exception {
        ItemRequestDtoResponse requestShortDto = new ItemRequestDtoResponse("testDescription");

        JsonContent<ItemRequestDtoResponse> result = json.write(requestShortDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");
    }
}