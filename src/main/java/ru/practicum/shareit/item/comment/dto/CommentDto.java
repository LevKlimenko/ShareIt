package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Getter
@Setter
@Builder
public class CommentDto {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}