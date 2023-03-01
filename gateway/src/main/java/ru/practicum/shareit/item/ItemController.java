package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.ItemIncomingDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @Valid @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.getAllByUserId(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody @Validated(Create.class) ItemIncomingDto itemIncomingDto) {
        return itemClient.save(userId, itemIncomingDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemIncomingDto itemIncomingDto) {
        return itemClient.update(itemId, userId, itemIncomingDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByRequest(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        if (text.isBlank()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
        return itemClient.findByRequest(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId,
                                                @Valid @RequestBody CommentIncomingDto commentIncomingDto) {
        return itemClient.createComment(userId, itemId, commentIncomingDto);
    }
}