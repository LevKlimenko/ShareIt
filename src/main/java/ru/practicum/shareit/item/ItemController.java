package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> usersItem = itemService.findByUserId(userId);
        log.info("The user's items have been received for UserID={}", userId);
        return usersItem;
    }

    @PostMapping
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @RequestBody @Validated(Create.class) ItemInDto item) {
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

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteById(itemId, userId);
        log.info("The user's item have been deleted for UserID={}, ItemID={}", userId, itemId);

    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
        ItemDto item = itemService.findById(userId, itemId);
        log.info("The item was found, ItemID={}", item.getId());
        return item;
    }

    @GetMapping("/search")
    public List<ItemDto> findByRequest(@RequestParam String text) {
        if (text.isBlank()) {
            log.info("No items for empty request");
            return List.of();
        }
        List<ItemDto> items = itemService.findByString(text);
        log.info("Items were found on request '{}'", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId,
                                    @Valid @RequestBody CommentIncomingDto commentIncomingDto) {
        CommentDto commentDto = itemService.createComment(userId, itemId, commentIncomingDto);
        log.info("Comment from user id={} for item id={} have been add", userId, itemId);
        return commentDto;
    }
}