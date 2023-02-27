package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping()
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestDtoResponse requestDto) {
        return requestService.create(userId, requestDto);
    }

    @GetMapping()
    public List<ItemRequestDto> findRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(required = false) int from,
                                                    @RequestParam(required = false) int size) {
        return requestService.findAllByOwner(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false) int from,
                                        @RequestParam(required = false) int size) {
        return requestService.findAll(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        return requestService.get(userId, requestId);
    }

}