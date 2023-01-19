package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse addItemRequest(Long userId, ItemRequestDto itemRequestDto);
    List <ItemRequestDtoResponse> findAllByOwner(Long userId);
    List<ItemRequestDtoResponse> findAll (Long userId, Pageable pageable);
    ItemRequestDtoResponse findRequest(Long userId, Long requestId);

}
