package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long id, ItemInDto item);

    ItemDto update(Long itemId, Long userId, ItemInDto item);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findByUserId(Long userId, int from, int size);

    List<ItemDto> findByString(String s, int from, int size);

    CommentDto createComment(Long userId, Long itemId, CommentIncomingDto commentIncomingDto);
}