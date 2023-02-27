package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentIncomingDtoTest {

    @Autowired
    private JacksonTester<CommentIncomingDto> json;

    @Test
    void testCommentDto() throws Exception {
        CommentIncomingDto commentDto = new CommentIncomingDto("comment");

        JsonContent<CommentIncomingDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");

    }
}
