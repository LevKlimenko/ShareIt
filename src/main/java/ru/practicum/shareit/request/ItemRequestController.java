package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping()
    public ItemRequestDtoResponse addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Validated ({Create.class}) @RequestBody ItemRequestDto requestDto){
        ItemRequestDtoResponse request = requestService.addItemRequest(userId, requestDto);
        log.info("Adding a new request successfully. ID= {}, description: {}", request.getId(),request.getDescription());
        return request;
    }

    @GetMapping()
    public List<ItemRequestDtoResponse> findRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId){
        return requestService.findAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PositiveOrZero @RequestParam(name = "from",defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(name ="size",defaultValue = "10")
                                                Integer size){
        int page = from/size;
        return requestService.findAll(userId, PageRequest.of(page,size));
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoResponse findRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long requestId){
        return requestService.findRequest(userId, requestId);
    }

}