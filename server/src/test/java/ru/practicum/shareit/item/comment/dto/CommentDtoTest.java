package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("authorName")
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("authorName");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotNull();
    }
}
