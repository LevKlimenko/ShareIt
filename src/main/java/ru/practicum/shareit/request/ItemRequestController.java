package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping()
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDtoResponse requestDto) {
        //log.info("Adding a new request successfully. ID= {}, description: {}", request.getId(),request.getDescription());
        return requestService.create(userId, requestDto);
    }

    @GetMapping()
    public List<ItemRequestDto> findRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.findAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Min(0) @RequestParam(defaultValue = "0")
                                        int from,
                                        @Min(1) @RequestParam(defaultValue = "10")
                                        int size) {
        int page = from / size;
        return requestService.findAll(userId, PageRequest.of(page, size));
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        return requestService.get(userId, requestId);
    }

}