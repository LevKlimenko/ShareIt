package ru.practicum.shareit.item.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName((comment.getAuthor() != null ? comment.getAuthor().getName() : ""))
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentIncomingDto incomingDto, User author, Item item) {
        return Comment.builder()
                .text(incomingDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }
}