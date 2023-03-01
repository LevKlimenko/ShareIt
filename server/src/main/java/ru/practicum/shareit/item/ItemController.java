package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false) int from,
                                        @RequestParam(required = false) int size) {
        List<ItemDto> usersItem = itemService.findByUserId(userId, from, size);
        log.info("The user's items have been received for UserID={}", userId);
        return usersItem;
    }

    @PostMapping
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @RequestBody ItemInDto item) {
        ItemDto addedItem = itemService.save(userId, item);
        log.info("The user's item have been add for UserID={}, ItemID={}", userId, addedItem.getId());
        return addedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemInDto item) {
        ItemDto upItem = itemService.update(itemId, userId, item);
        log.info("The user's item have been update for UserID={}, ItemID={}", userId, upItem.getId());
        return upItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
        ItemDto item = itemService.findById(userId, itemId);
        log.info("The item was found, ItemID={}", item.getId());
        return item;
    }

    @GetMapping("/search")
    public List<ItemDto> findByRequest(@RequestParam String text,
                                       @RequestParam(required = false) int from,
                                       @RequestParam(required = false) int size) {
        if (text.isBlank()) {
            log.info("No items for empty request");
            return List.of();
        }
        List<ItemDto> items = itemService.findByString(text, from, size);
        log.info("Items were found on request '{}'", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId,
                                    @RequestBody CommentIncomingDto commentIncomingDto) {
        CommentDto commentDto = itemService.createComment(userId, itemId, commentIncomingDto);
        log.info("Comment from user id={} for item id={} have been add", userId, itemId);
        return commentDto;
    }
}