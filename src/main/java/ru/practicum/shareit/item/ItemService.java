package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long id, ItemInDto item);

    ItemDto update(Long itemId, Long userId, ItemInDto item);

    void deleteById(Long itemId, Long userId);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findByUserId(Long userId);

    List<ItemDto> findByString(String s);

    CommentDto createComment(Long userId, Long itemId, CommentIncomingDto commentIncomingDto);
}